package io.github.kuromeforever.thefooldarkdoppelgangermorph.compat;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.TheFoolDarkDoppelgangerMorph;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Exact 9.8.2 player-visible literal bridge. Unknown strings remain untouched. */
public final class DarkDoppelgangerSourceTranslations {
    private static final String PREFIX = "message." + TheFoolDarkDoppelgangerMorph.MOD_ID + ".source.";
    private static final Pattern PLAYER_MINION = Pattern.compile("^(.+)'s Minion$");
    private static final Pattern COMMAND_NONE = Pattern.compile(
            "^No Dark Doppelganger entities found in radius ([0-9]+)\\.$"
    );
    private static final Pattern COMMAND_REMOVED = Pattern.compile(
            "^Removed ([0-9]+) Dark Doppelganger entity\\(ies\\) in radius ([0-9]+) "
                    + "\\(bosses: ([0-9]+), clones/minions: ([0-9]+)\\)\\.$"
    );
    private static final Map<String, String> EXACT = Map.ofEntries(
            Map.entry("The Dark Doppelganger has returned from the void...", PREFIX + "void_return"),
            Map.entry("The Dark Doppelganger has entered its Second Phase!", PREFIX + "second_phase"),
            Map.entry("Final Form! Prepare yourself!", PREFIX + "final_form"),
            Map.entry("You have slain the Dark Doppelganger!", PREFIX + "slain"),
            Map.entry("Minion", PREFIX + "minion"),
            Map.entry("Dark Doppelganger Minion", "entity.darkdoppelganger.dark_doppelganger_minion"),
            Map.entry("You already have a minion summoned.", PREFIX + "minion_exists"),
            Map.entry("No players nearby to copy.", PREFIX + "command.no_players"),
            Map.entry("An error occurred while executing the command.", PREFIX + "command.error"),
            Map.entry("Target player is no longer valid.", PREFIX + "command.target_invalid"),
            Map.entry("Item is depreciated.", PREFIX + "scroll.deprecated"),
            Map.entry("\u00a7dDepreciated", PREFIX + "scroll.deprecated"),
            Map.entry("\u00a77This item no longer works", PREFIX + "scroll.no_longer_works")
    );

    private DarkDoppelgangerSourceTranslations() {
    }

    public static MutableComponent translateLiteral(String original) {
        if ("Dark Doppelganger".equals(original)) {
            return Component.translatable("entity.darkdoppelganger.dark_doppelganger");
        }
        if ("Dark Doppelganger - Final Phase".equals(original)) {
            return Component.translatable(PREFIX + "final_phase_name");
        }
        String exactKey = EXACT.get(original);
        if (exactKey != null) {
            MutableComponent translated = Component.translatable(exactKey);
            if ("\u00a7dDepreciated".equals(original)) {
                return translated.withStyle(ChatFormatting.LIGHT_PURPLE);
            }
            if ("\u00a77This item no longer works".equals(original)) {
                return translated.withStyle(ChatFormatting.GRAY);
            }
            return translated;
        }
        Matcher playerMinion = PLAYER_MINION.matcher(original);
        if (playerMinion.matches()) {
            return Component.translatable(PREFIX + "player_minion", playerMinion.group(1));
        }
        Matcher none = COMMAND_NONE.matcher(original);
        if (none.matches()) {
            return Component.translatable(PREFIX + "command.none", none.group(1));
        }
        Matcher removed = COMMAND_REMOVED.matcher(original);
        if (removed.matches()) {
            return Component.translatable(
                    PREFIX + "command.removed",
                    removed.group(1), removed.group(2), removed.group(3), removed.group(4)
            );
        }
        return Component.literal(original);
    }

    public static boolean isTranslatedBossBarName(String renderedName) {
        if (renderedName == null) {
            return false;
        }
        String normal = Component.translatable("entity.darkdoppelganger.dark_doppelganger").getString();
        String finale = Component.translatable(PREFIX + "final_phase_name").getString();
        return renderedName.startsWith("Dark Doppelganger")
                || renderedName.startsWith(normal)
                || renderedName.startsWith(finale);
    }

    public static MutableComponent spawnCountdown(Component copiedPlayerName) {
        return Component.translatable(PREFIX + "command.spawn_countdown", copiedPlayerName);
    }
}
