package io.github.kuromeforever.thefooldarkdoppelgangermorph.runtime;

import com.google.gson.JsonParser;
import software.bernie.geckolib.core.animation.RawAnimation;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** Source asset timing, loaded once, including on a dedicated server. */
public final class DoppelAnimationLengths {
    private static final Map<String, Double> LENGTHS = load();
    private DoppelAnimationLengths() { }

    public static int finishTicks(RawAnimation animation) {
        if (animation == null) return 0;
        double ticks = 0;
        for (var stage : animation.getAnimationStages()) {
            // Unknown clips have a bounded one-second recovery instead of a six-second pose lock.
            ticks += LENGTHS.getOrDefault(stage.animationName(), 1.0) * 20 + stage.additionalTicks();
        }
        return Math.max(1, Math.min(120, (int) Math.ceil(ticks) + 2));
    }

    private static Map<String, Double> load() {
        Map<String, Double> lengths = new HashMap<>();
        for (String asset : new String[]{
                "/assets/traveloptics/animations/casting_animations.json",
                "/assets/darkdoppelganger/animations/doppel_casting_animations.json"}) {
            try (var input = DoppelAnimationLengths.class.getResourceAsStream(asset)) {
                if (input == null) continue;
                var animations = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8))
                        .getAsJsonObject().getAsJsonObject("animations");
                for (var entry : animations.entrySet()) {
                    var length = entry.getValue().getAsJsonObject().get("animation_length");
                    if (length != null && length.getAsDouble() > 0) lengths.put(entry.getKey(), length.getAsDouble());
                }
            } catch (java.io.IOException | RuntimeException failure) {
                com.mojang.logging.LogUtils.getLogger().warn("Cannot read Doppel source animation lengths: {}", asset, failure);
            }
        }
        return Map.copyOf(lengths);
    }
}
