package io.github.kuromeforever.thefooldarkdoppelgangermorph.morph;

import com.kurome.ageofmythology.api.morph.AomMorphApi;
import com.kurome.ageofmythology.api.morph.MorphApiVersion;
import com.kurome.ageofmythology.api.morph.MorphSkillRegistrar;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.TheFoolDarkDoppelgangerMorph;
import net.bandit.darkdoppelganger.entity.EntityRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Stable constructor-time declaration consumed by AOM Morph API v1.1. */
public final class DarkDoppelgangerMorphRegistration {
    public static final ResourceLocation PROVIDER_ID = skill("dark_doppelganger");
    public static final ResourceLocation BOSS_ID = source("dark_doppelganger");
    public static final ResourceLocation MINION_ID = source("dark_doppelganger_minion");
    public static final List<ResourceLocation> ENTITY_ORDER = List.of(BOSS_ID);
    public static final Set<ResourceLocation> TECHNICAL_IDENTITIES = Set.of(MINION_ID);
    public static final Map<ResourceLocation, List<ResourceLocation>> ACTIVE_SKILLS = activeSkills();

    private DarkDoppelgangerMorphRegistration() {
    }

    public static void bootstrap() {
        MorphApiVersion apiVersion = AomMorphApi.version();
        if (apiVersion.major() != 1 || apiVersion.compareTo(MorphApiVersion.V1_1) < 0) {
            TheFoolDarkDoppelgangerMorph.LOGGER.error(
                    "Unsupported AOM Morph API version {}; Dark Doppelganger provider was skipped",
                    apiVersion
            );
            return;
        }
        if (!AomMorphApi.registerProvider(PROVIDER_ID, DarkDoppelgangerMorphRegistration::register)) {
            TheFoolDarkDoppelgangerMorph.LOGGER.error(
                    "AOM rejected Dark Doppelganger morph provider {}; game loading will continue",
                    PROVIDER_ID
            );
        }
    }

    private static void register(MorphSkillRegistrar registrar) {
        var boss = registrar.entity(BOSS_ID, EntityRegistry.DARK_DOPPELGANGER::get)
                .ability(skill("mirror_blade_combo"), DarkDoppelgangerAbilities::mirrorBladeCombo)
                .ability(skill("shadow_slash"), DarkDoppelgangerAbilities::shadowSlash)
                .ability(skill("doppel_portal"), DarkDoppelgangerAbilities::doppelPortal)
                .ability(skill("summon_doppel_minion"), DarkDoppelgangerAbilities::summonDoppelMinion)
                .ability(skill("life_drain"), DarkDoppelgangerAbilities::lifeDrain)
                .identity(DarkDoppelgangerIdentity::new);
        for (var spell : DarkDoppelgangerSpellCatalog.ADDITIONS) {
            boss.ability(skill(spell.key()), () -> DarkDoppelgangerAbilities.additionalSpell(spell));
        }
        registrar.entity(MINION_ID, EntityRegistry.DARK_DOPPELGANGER_MINION::get)
                .technicalIdentity();
    }

    private static Map<ResourceLocation, List<ResourceLocation>> activeSkills() {
        Map<ResourceLocation, List<ResourceLocation>> skills = new LinkedHashMap<>();
        var entries = new java.util.ArrayList<ResourceLocation>(List.of(
                skill("mirror_blade_combo"),
                skill("shadow_slash"),
                skill("doppel_portal"),
                skill("summon_doppel_minion"),
                skill("life_drain")
        ));
        DarkDoppelgangerSpellCatalog.ADDITIONS.forEach(entry -> entries.add(skill(entry.key())));
        skills.put(BOSS_ID, List.copyOf(entries));
        return Collections.unmodifiableMap(skills);
    }

    public static ResourceLocation source(String path) {
        return ResourceLocation.fromNamespaceAndPath("darkdoppelganger", path);
    }

    public static ResourceLocation skill(String path) {
        return ResourceLocation.fromNamespaceAndPath(TheFoolDarkDoppelgangerMorph.MOD_ID, path);
    }
}
