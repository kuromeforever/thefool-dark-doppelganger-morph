package io.github.kuromeforever.thefooldarkdoppelgangermorph;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipFile;
import static org.junit.jupiter.api.Assertions.*;

class NoCastProjectionContractTest {
    private static final String ROOT = "io/github/kuromeforever/thefooldarkdoppelgangermorph/";

    @Test void sourcePredicateDescriptorsAndVisibilityMatchEveryNewInjection() throws Exception {
        try (var source = new ZipFile(ContractTestSupport.sourceJar().toFile());
             var iron = new ZipFile(System.getProperty("thefool.iron.contract.jar"))) {
            var doppel = ContractTestSupport.classNode(source, "net/bandit/darkdoppelganger/entity/DarkDoppelgangerEntity");
            var mob = ContractTestSupport.classNode(iron,
                    "io/redspace/ironsspellbooks/entity/mobs/abstract_spell_casting_mob/AbstractSpellCastingMob");
            String predicate = "(Lsoftware/bernie/geckolib/core/animation/AnimationState;)Lsoftware/bernie/geckolib/core/object/PlayState;";
            method(doppel, "predicate", predicate);
            method(doppel, "isAnimating", "()Z");
            for (String name : List.of("instantCastingPredicate", "longCastingPredicate", "otherCastingPredicate"))
                assertTrue((method(mob, name, predicate).access & Opcodes.ACC_PRIVATE) != 0, name);
            method(mob, "shouldPointArmsWhileCasting", "()Z");
        }
    }

    @Test void shippedSkillBusinessNeverStartsVisualSessionsAndKeepsNativeReceipts() throws Exception {
        try (var jar = new ZipFile(ContractTestSupport.adapterJar().toFile())) {
            for (String name : List.of("runtime/DarkDoppelgangerSpellSessions", "runtime/BladeComboService",
                    "morph/DarkDoppelgangerAbilities")) {
                var type = ContractTestSupport.classNode(jar, ROOT + name);
                assertEquals(0, type.methods.stream().flatMap(m -> Arrays.stream(m.instructions.toArray()))
                        .filter(i -> i instanceof MethodInsnNode call
                                && call.owner.equals(ROOT + "network/DarkDoppelgangerActionSessions")
                                && call.name.startsWith("start")).count(), name);
            }
            var sessions = ContractTestSupport.classNode(jar, ROOT + "runtime/DarkDoppelgangerSpellSessions");
            for (String call : List.of("cast", "captureNativeSession", "cancelNativeSession"))
                assertTrue(sessions.methods.stream().flatMap(m -> Arrays.stream(m.instructions.toArray()))
                        .anyMatch(i -> i instanceof MethodInsnNode m
                                && m.owner.endsWith("MorphSpellCastCoordinator") && m.name.equals(call)), call);
        }
    }

    @Test void latePacketSinkCannotBindCarrierOrRestartController() throws Exception {
        try (var jar = new ZipFile(ContractTestSupport.adapterJar().toFile())) {
            var type = ContractTestSupport.classNode(jar, ROOT + "client/DarkDoppelgangerClientActions");
            var handle = method(type, "handle", "(L" + ROOT + "network/ClientboundDarkDoppelgangerAction;)V");
            assertEquals(0, Arrays.stream(handle.instructions.toArray()).filter(i ->
                    i instanceof MethodInsnNode || i instanceof FieldInsnNode || i instanceof InvokeDynamicInsnNode).count());
            assertTrue(type.fields.isEmpty(), "No pending packet map may restore disabled actions");
        }
    }

    private static MethodNode method(ClassNode type, String name, String descriptor) {
        return type.methods.stream().filter(m -> m.name.equals(name) && m.desc.equals(descriptor))
                .findFirst().orElseThrow(() -> new AssertionError(type.name + "#" + name + descriptor));
    }
}
