package io.github.kuromeforever.thefooldarkdoppelgangermorph;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MorphCatalogAndRuntimeContractTest {
    private static final String REGISTRATION =
            "src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph/morph/DarkDoppelgangerMorphRegistration.java";
    private static final String ABILITIES =
            "src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph/morph/DarkDoppelgangerAbilities.java";

    @Test
    void registersOnePlayableBossOneTechnicalMinionAndExactlyTwentySixSkills() throws Exception {
        String source = ContractTestSupport.source(REGISTRATION);
        assertTrue(source.contains("MorphApiVersion.V1_1"));
        assertTrue(source.contains("BOSS_ID = source(\"dark_doppelganger\")"));
        assertTrue(source.contains("MINION_ID = source(\"dark_doppelganger_minion\")"));
        assertEquals(6, count(source, ".ability("));
        String catalog = ContractTestSupport.source("src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph/morph/DarkDoppelgangerSpellCatalog.java");
        assertEquals(21, count(catalog, "new Entry("));
        assertTrue(source.contains("DarkDoppelgangerSpellCatalog.ADDITIONS"));
        assertEquals(1, count(source, ".identity("));
        assertEquals(1, count(source, ".technicalIdentity()"));
        for (String skill : Set.of(
                "mirror_blade_combo", "shadow_slash", "doppel_portal",
                "summon_doppel_minion", "life_drain"
        )) {
            assertTrue(source.contains("skill(\"" + skill + "\")"), skill);
        }
        assertFalse(source.contains("Class.forName"));
        assertFalse(source.contains("java.lang.reflect"));
    }

    @Test
    void allSkillsUseOnlyTheAomSixtyTickGlobalCooldown() throws Exception {
        String abilities = ContractTestSupport.source(ABILITIES);
        assertTrue(abilities.contains("GLOBAL_ABILITY_COOLDOWN_TICKS = 3 * 20"));
        assertTrue(abilities.contains("MAX_ACTION_TICKS = 3 * 20"));
        assertEquals(1, count(abilities, ".setCooldown(GLOBAL_ABILITY_COOLDOWN_TICKS)"));

        StringBuilder merged = new StringBuilder();
        try (var paths = Files.walk(ContractTestSupport.projectRoot().resolve("src/main/java"))) {
            for (var path : paths.filter(candidate -> candidate.toString().endsWith(".java")).toList()) {
                merged.append(Files.readString(path));
            }
        }
        for (String forbidden : Set.of(
                "setCooldownResolver", "readyAt", "CooldownService", "abilityCooldown",
                "PlayerAbilities.setCooldown", "CompoundTag cooldown"
        )) {
            assertFalse(merged.toString().contains(forbidden), forbidden);
        }
    }

    @Test
    void mirrorBladeLocksFourDistinctFormsChanceNodesAndDamageBudget() throws Exception {
        String blade = ContractTestSupport.source(
                "src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph/runtime/BladeComboService.java"
        );
        assertTrue(blade.contains("nextFloat() < 0.4F"));
        assertTrue(blade.contains("1 + player.getRandom().nextInt(FORMS.length - 1)"));
        assertTrue(blade.contains("hit(player, session.targetId(), 12.0F)"));
        assertTrue(blade.contains("hit(player, session.targetId(), 8.0F)"));
        assertTrue(blade.contains("UPWARD(DarkDoppelgangerAction.BLADE_UPWARD, 9, 5)"));
        assertTrue(blade.contains("LUNGE(DarkDoppelgangerAction.BLADE_LUNGE, 8, 6)"));
        assertTrue(blade.contains("STAB(DarkDoppelgangerAction.BLADE_STAB, 10, 8)"));
        assertTrue(blade.contains("CROSS(DarkDoppelgangerAction.BLADE_CROSS, 10, 8)"));
        assertTrue(blade.contains("new Session(frozenTarget.getUUID(), first, second)"));
        assertFalse(blade.contains("getTarget()"));
        assertFalse(blade.contains("addFreshEntity"));
    }

    @Test
    void nativeSpellsAndMinionReuseSourceOwnershipContracts() throws Exception {
        String abilities = ContractTestSupport.source(ABILITIES);
        assertTrue(abilities.contains("api.registry.SpellRegistry.SHADOW_SLASH::get"));
        assertTrue(abilities.contains("registry.SpellRegistry.DOPPEL_PORTAL::get"));
        assertTrue(abilities.contains("registry.SpellRegistry.MINION_SPELL::get"));
        String nativeSessions = ContractTestSupport.source("src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph/runtime/DarkDoppelgangerSpellSessions.java");
        assertEquals(1, count(nativeSessions, "MorphSpellCastCoordinator.cast("));
        assertTrue(nativeSessions.contains("MorphSpellCastCoordinator.ExecutionMode.NATIVE_SESSION"));
        assertTrue(abilities.contains("SummonDoppelMinionSpellInvoker.thefoolDarkDoppelgangerMorph$hasLivingMinion"));
        assertFalse(abilities.contains("PlayerMinionUUID"));
        assertFalse(abilities.contains("addFreshEntity"));

        String invoker = ContractTestSupport.source(
                "src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/accessor/SummonDoppelMinionSpellInvoker.java"
        );
        assertTrue(invoker.contains("@Invoker(\"hasLivingMinion\")"));
        assertTrue(invoker.contains("ServerLevel level"));
        assertTrue(invoker.contains("ServerPlayer player"));
    }

    @Test
    void lifeDrainIsStableBoundedAndHealsOnlyMeasuredLoss() throws Exception {
        String abilities = ContractTestSupport.source(ABILITIES);
        assertTrue(abilities.contains("LIFE_DRAIN_RADIUS = 8.0D"));
        assertTrue(abilities.contains("LIFE_DRAIN_MAX_TARGETS = 8"));
        assertTrue(abilities.contains("LIFE_DRAIN_DAMAGE = 4.0F"));
        assertTrue(abilities.contains("LIFE_DRAIN_MAX_HEAL = 16.0F"));
        assertTrue(abilities.contains("hurtAndMeasureHealthLoss"));
        assertTrue(abilities.contains("Math.min(LIFE_DRAIN_MAX_HEAL, heal + lost)"));

        String combat = ContractTestSupport.source(
                "src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph/runtime/DarkDoppelgangerCombat.java"
        );
        assertTrue(combat.contains("thenComparing(candidate -> candidate.getUUID().toString())"));
        assertTrue(combat.contains("player.canHarmPlayer(targetPlayer)"));
        assertTrue(combat.contains("targetPlayer.isCreative()"));
        assertTrue(combat.contains("targetPlayer.isSpectator()"));
        assertTrue(combat.contains("tamable.isOwnedBy(player)"));
        assertTrue(combat.contains("player.getUUID().equals(minion.getSummonerUUID())"));
        assertTrue(combat.contains("before - target.getHealth()"));
    }

    private static int count(String text, String needle) {
        int result = 0;
        int index = 0;
        while ((index = text.indexOf(needle, index)) >= 0) {
            result++;
            index += needle.length();
        }
        return result;
    }
}
