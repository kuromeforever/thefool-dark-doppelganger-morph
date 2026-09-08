package io.github.kuromeforever.thefooldarkdoppelgangermorph;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import java.util.Set;
import java.util.zip.ZipFile;
import static org.junit.jupiter.api.Assertions.*;

class ProjectionSourceBoundaryTest {
    @Test void sourcePrivateAgeAndBirthAnimationExplainTheFixedFailure() throws Exception {
        try (ZipFile source = new ZipFile(ContractTestSupport.sourceJar().toFile())) {
            ClassNode type = ContractTestSupport.classNode(source, "net/bandit/darkdoppelganger/entity/DarkDoppelgangerEntity");
            var age = type.fields.stream().filter(f -> f.name.equals("age")).findFirst().orElseThrow();
            assertEquals("I", age.desc); assertTrue((age.access & Opcodes.ACC_PRIVATE) != 0);
            for (String name : Set.of("predicate", "spawnPredicate")) {
                var method = type.methods.stream().filter(m -> m.name.equals(name)).findFirst().orElseThrow();
                assertTrue(java.util.Arrays.stream(method.instructions.toArray()).anyMatch(i ->
                        i instanceof FieldInsnNode field && field.owner.equals(type.name) && field.name.equals("age")));
            }
            var clip = JsonParser.parseString(ContractTestSupport.zipText(source,
                    "assets/darkdoppelganger/animations/doppel_casting_animations.json")).getAsJsonObject()
                    .getAsJsonObject("animations").getAsJsonObject("join_1");
            assertEquals(1.75, clip.get("animation_length").getAsDouble());
            var scales = clip.getAsJsonObject("bones").getAsJsonObject("body").getAsJsonObject("scale");
            assertTrue(scales.get("0.5417").toString().contains("0"));
            assertTrue(scales.has("0.7917"));
        }
    }
    @Test void nativeCompletionObserverMatchesBothCurrentIronCallSites() throws Exception {
        try (ZipFile iron = new ZipFile(System.getProperty("thefool.iron.contract.jar"))) {
            ClassNode manager = ContractTestSupport.classNode(iron, "io/redspace/ironsspellbooks/capabilities/magic/MagicManager");
            ClassNode caster = ContractTestSupport.classNode(iron,
                    "io/redspace/ironsspellbooks/entity/mobs/abstract_spell_casting_mob/AbstractSpellCastingMob");
            assertTrue(caster.fields.stream().anyMatch(f -> f.name.equals("animatingLegs") && f.desc.equals("Z")));
            var tick = manager.methods.stream().filter(m -> m.name.equals("lambda$tick$0")).findFirst().orElseThrow();
            assertFalse((tick.access & Opcodes.ACC_STATIC) != 0);
            assertEquals(2, java.util.Arrays.stream(tick.instructions.toArray()).filter(i ->
                    i instanceof MethodInsnNode call && call.owner.equals("io/redspace/ironsspellbooks/api/spells/AbstractSpell")
                            && call.name.equals("onServerCastComplete")
                            && call.desc.equals("(Lnet/minecraft/world/level/Level;ILnet/minecraft/world/entity/LivingEntity;Lio/redspace/ironsspellbooks/api/magic/MagicData;Z)V")).count());
        }
    }
    @Test void travelSourceProvidesAllFiveAnimationClipsAndMatchingHumanoidBones() throws Exception {
        try (ZipFile travel = new ZipFile(System.getProperty("thefool.travel.contract.jar"));
             ZipFile source = new ZipFile(ContractTestSupport.sourceJar().toFile())) {
            var animations = JsonParser.parseString(ContractTestSupport.zipText(travel,
                    "assets/traveloptics/animations/casting_animations.json")).getAsJsonObject().getAsJsonObject("animations");
            var sourceClips = JsonParser.parseString(ContractTestSupport.zipText(source,
                    "assets/darkdoppelganger/animations/doppel_casting_animations.json")).getAsJsonObject().getAsJsonObject("animations");
            for (String name : Set.of("miasma_start", "miasma_end", "spectral_blink", "tidal_grasp", "tidal_grasp_smack")) {
                assertTrue(animations.has(name), name);
                assertFalse(sourceClips.has(name), "The adapter must deliberately route this external resource");
                assertTrue(animations.getAsJsonObject(name).get("animation_length").getAsDouble() <= 6);
                assertTrue(Set.of("body", "head", "torso", "right_arm", "left_arm", "right_leg", "left_leg")
                        .containsAll(animations.getAsJsonObject(name).getAsJsonObject("bones").keySet()));
            }
        }
    }
    @Test void projectionPreparationCannotRunNativeAiAndOnlyOwnsExactReferences() throws Exception {
        String root = "src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph/";
        String presentation = ContractTestSupport.source(root + "client/DarkDoppelgangerPresentation.java");
        assertTrue(presentation.contains("PlayerIdentity.getIdentity(player) == identity"));
        assertTrue(presentation.contains("identity.level().getEntity(identity.getId()) != identity"));
        assertTrue(presentation.contains("previous.update(player, identity)"));
        for (String prohibited : Set.of(".tick()", "onAddedToWorld(", "finalizeSpawn(", "setHealth(", "setBossMinion("))
            assertFalse(presentation.contains(prohibited), prohibited);
        String lifecycle = ContractTestSupport.source(root + "lifecycle/DarkDoppelgangerLifecycle.java");
        assertFalse(lifecycle.contains("MorphSpellCastCoordinator.cancel("));
        String sessions = ContractTestSupport.source(root + "runtime/DarkDoppelgangerSpellSessions.java");
        assertTrue(sessions.contains("cancelNativeSession(player, receipt.nativeSession)"));
        assertTrue(sessions.contains("isNativeSessionActive(player, nativeSession)"));
        assertTrue(sessions.contains("ACTIVE.remove(receipt.player.getUUID(), receipt)"));
        assertFalse(sessions.contains("removeEffect("));
    }
}
