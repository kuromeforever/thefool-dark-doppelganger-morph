package io.github.kuromeforever.thefooldarkdoppelgangermorph.network;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.TheFoolDarkDoppelgangerMorph;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;

/** Stable protocol IDs and their exact source GeckoLib clips. */
public final class DarkDoppelgangerAction {
    public static final ResourceLocation BLADE_UPWARD = id("blade_upward");
    public static final ResourceLocation BLADE_LUNGE = id("blade_lunge");
    public static final ResourceLocation BLADE_STAB = id("blade_stab");
    public static final ResourceLocation BLADE_CROSS = id("blade_cross");
    public static final ResourceLocation SHADOW_SLASH = id("shadow_slash");
    public static final ResourceLocation DOPPEL_PORTAL = id("doppel_portal");
    public static final ResourceLocation SUMMON_DOPPEL_MINION = id("summon_doppel_minion");
    public static final ResourceLocation LIFE_DRAIN = id("life_drain");

    private static final Map<ResourceLocation, String> ANIMATIONS = Map.of(
            BLADE_UPWARD, "simple_sword_upward_swipe",
            BLADE_LUNGE, "simple_sword_lunge_stab",
            BLADE_STAB, "simple_sword_stab_alternate",
            BLADE_CROSS, "simple_sword_horizontal_cross_swipe",
            SHADOW_SLASH, "instant_slash",
            DOPPEL_PORTAL, "long_cast",
            SUMMON_DOPPEL_MINION, "long_cast",
            LIFE_DRAIN, "instant_self"
    );
    public static final Set<ResourceLocation> ALL = Set.copyOf(ANIMATIONS.keySet());

    private DarkDoppelgangerAction() {
    }

    public static boolean isKnown(ResourceLocation actionId) {
        return actionId != null && (ALL.contains(actionId) ||
                io.github.kuromeforever.thefooldarkdoppelgangermorph.morph.DarkDoppelgangerSpellCatalog.contains(actionId));
    }

    public static String animation(ResourceLocation actionId) {
        return ANIMATIONS.get(actionId);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(TheFoolDarkDoppelgangerMorph.MOD_ID, path);
    }
}
