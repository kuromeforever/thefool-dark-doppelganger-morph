package io.github.kuromeforever.thefooldarkdoppelgangermorph;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DarkDoppelganger982SourceContractTest {
    private static final String EXPECTED_SHA256 =
            "A40E7AC6599A208DA8BCEC747EA2630C3DBB903D3C3353C860EBCCA62BADF988";
    private static final String COMPONENT_LITERAL_DESC =
            "(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;";

    @Test
    void locksExactArtifactMetadataLanguageAndSounds() throws Exception {
        assertEquals(EXPECTED_SHA256, ContractTestSupport.sha256(ContractTestSupport.sourceJar()));
        try (ZipFile zip = new ZipFile(ContractTestSupport.sourceJar().toFile())) {
            String modsToml = ContractTestSupport.zipText(zip, "META-INF/mods.toml");
            assertTrue(modsToml.contains("modId = \"darkdoppelganger\""));
            assertTrue(modsToml.contains("version = \"9.8.2-1.20.1\""));
            assertTrue(modsToml.contains("license = \"MIT\""));

            JsonObject english = JsonParser.parseString(ContractTestSupport.zipText(
                    zip, "assets/darkdoppelganger/lang/en_us.json"
            )).getAsJsonObject();
            assertEquals(20, english.size());
            assertFalse(zip.stream().anyMatch(entry -> entry.getName().equals(
                    "assets/darkdoppelganger/lang/zh_cn.json"
            )));

            JsonObject sounds = JsonParser.parseString(ContractTestSupport.zipText(
                    zip, "assets/darkdoppelganger/sounds.json"
            )).getAsJsonObject();
            assertEquals(Set.of("boss_fight", "boss_laugh", "boss_roar", "boss_stun"), sounds.keySet());
            sounds.entrySet().forEach(entry -> assertFalse(entry.getValue().getAsJsonObject().has("subtitle")));
        }
    }

    @Test
    void locksPublicAnimationAndPrivateMinionGuardDescriptors() throws Exception {
        try (ZipFile zip = new ZipFile(ContractTestSupport.sourceJar().toFile())) {
            var boss = ContractTestSupport.classNode(
                    zip, "net/bandit/darkdoppelganger/entity/DarkDoppelgangerEntity"
            );
            assertNotNull(method(boss.methods, "playAnimation", "(Ljava/lang/String;)V"));

            var spell = ContractTestSupport.classNode(
                    zip, "net/bandit/darkdoppelganger/spells/SummonDoppelMinionSpell"
            );
            MethodNode guard = method(
                    spell.methods,
                    "hasLivingMinion",
                    "(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;)Z"
            );
            assertNotNull(guard);
            assertTrue((guard.access & Opcodes.ACC_PRIVATE) != 0);
            assertTrue((guard.access & Opcodes.ACC_STATIC) != 0);
        }
    }

    @Test
    void locksEveryPlayerVisibleLiteralBridgeTarget() throws Exception {
        Map<String, Set<MethodContract>> contracts = Map.of(
                "net/bandit/darkdoppelganger/entity/DarkDoppelgangerEntity", Set.of(
                        new MethodContract("<init>", "(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", 2,
                                Set.of("Dark Doppelganger")),
                        new MethodContract("m_8119_", "()V", 1,
                                Set.of("The Dark Doppelganger has returned from the void...")),
                        new MethodContract("triggerSecondPhase", "()V", 1,
                                Set.of("The Dark Doppelganger has entered its Second Phase!")),
                        new MethodContract("triggerThirdPhase", "()V", 2,
                                Set.of("Final Form! Prepare yourself!", "Dark Doppelganger - Final Phase")),
                        new MethodContract("summonMinions", "()V", 2,
                                Set.of("Minion")),
                        new MethodContract("m_6667_", "(Lnet/minecraft/world/damagesource/DamageSource;)V", 1,
                                Set.of("You have slain the Dark Doppelganger!"))
                ),
                "net/bandit/darkdoppelganger/entity/DarkDoppelgangerMinionEntity", Set.of(
                        new MethodContract("<init>", "(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", 1,
                                Set.of("Dark Doppelganger Minion"))
                ),
                "net/bandit/darkdoppelganger/spells/SummonDoppelMinionSpell", Set.of(
                        new MethodContract("onServerCastComplete", "(Lnet/minecraft/world/level/Level;ILnet/minecraft/world/entity/LivingEntity;Lio/redspace/ironsspellbooks/api/magic/MagicData;Z)V", 2,
                                Set.of("You already have a minion summoned.", "'s Minion"))
                ),
                "net/bandit/darkdoppelganger/item/SummonScrollItem", Set.of(
                        new MethodContract("m_6225_", "(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;", 1,
                                Set.of("Item is depreciated.")),
                        new MethodContract("m_7373_", "(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V", 2,
                                Set.of("\u00a7dDepreciated", "\u00a77This item no longer works"))
                ),
                "net/bandit/darkdoppelganger/command/ModCommands", Set.of(
                        new MethodContract("killDoppelgangers", "(Lnet/minecraft/commands/CommandSourceStack;I)I", 1,
                                Set.of("No Dark Doppelganger entities found in radius ")),
                        new MethodContract("lambda$killDoppelgangers$4", "(IIII)Lnet/minecraft/network/chat/Component;", 1,
                                Set.of("Removed ", " Dark Doppelganger entity(ies) in radius "))
                ),
                "net/bandit/darkdoppelganger/event/SummonDoppelganger", Set.of(
                        new MethodContract("summonDoppelganger", "(Lnet/minecraft/commands/CommandSourceStack;)I", 2,
                                Set.of("No players nearby to copy.", "An error occurred while executing the command.")),
                        new MethodContract("lambda$summonDoppelganger$1", "(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/commands/CommandSourceStack;)V", 1,
                                Set.of("Target player is no longer valid.")),
                        new MethodContract("lambda$summonDoppelganger$0", "(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/network/chat/Component;", 1,
                                Set.of("Dark Doppelganger will spawn in 5 seconds, copying "))
                )
        );
        try (ZipFile zip = new ZipFile(ContractTestSupport.sourceJar().toFile())) {
            for (var classContract : contracts.entrySet()) {
                var node = ContractTestSupport.classNode(zip, classContract.getKey());
                for (MethodContract contract : classContract.getValue()) {
                    MethodNode sourceMethod = method(node.methods, contract.name(), contract.descriptor());
                    assertNotNull(sourceMethod, classContract.getKey() + "." + contract.name());
                    assertEquals(contract.literalCalls(), componentLiteralCalls(sourceMethod), contract.name());
                    String joined = String.join("\n", stringConstants(sourceMethod));
                    contract.literalFragments().forEach(fragment -> assertTrue(
                            joined.contains(fragment), contract.name() + " missing " + fragment
                    ));
                }
            }
        }
    }

    @Test
    void locksEveryRedirectHandlerStaticnessAgainstItsSourceTarget() throws Exception {
        Set<RedirectHandlerContract> contracts = Set.of(
                new RedirectHandlerContract("net/bandit/darkdoppelganger/entity/DarkDoppelgangerEntity", "<init>",
                        "(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/DarkDoppelgangerEntityLocalizationMixin",
                        "thefoolDarkDoppelgangerMorph$translateConstructor"),
                new RedirectHandlerContract("net/bandit/darkdoppelganger/entity/DarkDoppelgangerEntity", "m_8119_", "()V",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/DarkDoppelgangerEntityLocalizationMixin",
                        "thefoolDarkDoppelgangerMorph$translateTick"),
                new RedirectHandlerContract("net/bandit/darkdoppelganger/entity/DarkDoppelgangerEntity", "triggerSecondPhase", "()V",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/DarkDoppelgangerEntityLocalizationMixin",
                        "thefoolDarkDoppelgangerMorph$translateSecondPhase"),
                new RedirectHandlerContract("net/bandit/darkdoppelganger/entity/DarkDoppelgangerEntity", "triggerThirdPhase", "()V",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/DarkDoppelgangerEntityLocalizationMixin",
                        "thefoolDarkDoppelgangerMorph$translateThirdPhase"),
                new RedirectHandlerContract("net/bandit/darkdoppelganger/entity/DarkDoppelgangerEntity", "summonMinions", "()V",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/DarkDoppelgangerEntityLocalizationMixin",
                        "thefoolDarkDoppelgangerMorph$translateBossMinions"),
                new RedirectHandlerContract("net/bandit/darkdoppelganger/entity/DarkDoppelgangerEntity", "m_6667_",
                        "(Lnet/minecraft/world/damagesource/DamageSource;)V",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/DarkDoppelgangerEntityLocalizationMixin",
                        "thefoolDarkDoppelgangerMorph$translateDeath"),
                new RedirectHandlerContract("net/bandit/darkdoppelganger/entity/DarkDoppelgangerMinionEntity", "<init>",
                        "(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/DarkDoppelgangerMinionLocalizationMixin",
                        "thefoolDarkDoppelgangerMorph$translateName"),
                new RedirectHandlerContract("net/bandit/darkdoppelganger/spells/SummonDoppelMinionSpell", "onServerCastComplete",
                        "(Lnet/minecraft/world/level/Level;ILnet/minecraft/world/entity/LivingEntity;Lio/redspace/ironsspellbooks/api/magic/MagicData;Z)V",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/SummonDoppelMinionSpellLocalizationMixin",
                        "thefoolDarkDoppelgangerMorph$translateSpell"),
                new RedirectHandlerContract("net/bandit/darkdoppelganger/item/SummonScrollItem", "m_6225_",
                        "(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/SummonScrollLocalizationMixin",
                        "thefoolDarkDoppelgangerMorph$translateUse"),
                new RedirectHandlerContract("net/bandit/darkdoppelganger/item/SummonScrollItem", "m_7373_",
                        "(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/SummonScrollLocalizationMixin",
                        "thefoolDarkDoppelgangerMorph$translateTooltip"),
                new RedirectHandlerContract("net/bandit/darkdoppelganger/command/ModCommands", "killDoppelgangers",
                        "(Lnet/minecraft/commands/CommandSourceStack;I)I",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/DarkDoppelgangerCommandLocalizationMixin",
                        "thefoolDarkDoppelgangerMorph$translateNone"),
                new RedirectHandlerContract("net/bandit/darkdoppelganger/command/ModCommands", "lambda$killDoppelgangers$4",
                        "(IIII)Lnet/minecraft/network/chat/Component;",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/DarkDoppelgangerCommandLocalizationMixin",
                        "thefoolDarkDoppelgangerMorph$translateRemoved"),
                new RedirectHandlerContract("net/bandit/darkdoppelganger/event/SummonDoppelganger", "summonDoppelganger",
                        "(Lnet/minecraft/commands/CommandSourceStack;)I",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/SummonDoppelgangerLocalizationMixin",
                        "thefoolDarkDoppelgangerMorph$translateCommandResult"),
                new RedirectHandlerContract("net/bandit/darkdoppelganger/event/SummonDoppelganger", "lambda$summonDoppelganger$1",
                        "(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/commands/CommandSourceStack;)V",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/SummonDoppelgangerLocalizationMixin",
                        "thefoolDarkDoppelgangerMorph$translateInvalidTarget"),
                new RedirectHandlerContract("net/bandit/darkdoppelganger/event/SummonDoppelganger", "lambda$summonDoppelganger$0",
                        "(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/network/chat/Component;",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/SummonDoppelgangerLocalizationMixin",
                        "thefoolDarkDoppelgangerMorph$translateCountdown"),
                new RedirectHandlerContract("net/bandit/darkdoppelganger/event/ClientEvents", "onCustomizeBossBar",
                        "(Lnet/minecraftforge/client/event/CustomizeGuiOverlayEvent$BossEventProgress;)V",
                        "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/client/DarkDoppelgangerBossBarMixin",
                        "thefoolDarkDoppelgangerMorph$acceptTranslatedName")
        );

        assertEquals(16, contracts.size());
        try (ZipFile source = new ZipFile(ContractTestSupport.sourceJar().toFile());
             ZipFile adapter = new ZipFile(ContractTestSupport.adapterJar().toFile())) {
            for (RedirectHandlerContract contract : contracts) {
                MethodNode sourceMethod = method(
                        ContractTestSupport.classNode(source, contract.sourceOwner()).methods,
                        contract.sourceMethod(),
                        contract.sourceDescriptor()
                );
                assertNotNull(sourceMethod, contract.sourceOwner() + "." + contract.sourceMethod());

                MethodNode handler = ContractTestSupport.classNode(adapter, contract.mixinOwner()).methods.stream()
                        .filter(candidate -> candidate.name.equals(contract.handlerMethod()))
                        .findFirst()
                        .orElse(null);
                assertNotNull(handler, contract.mixinOwner() + "." + contract.handlerMethod());

                boolean sourceStatic = (sourceMethod.access & Opcodes.ACC_STATIC) != 0;
                boolean handlerStatic = (handler.access & Opcodes.ACC_STATIC) != 0;
                assertEquals(sourceStatic, handlerStatic,
                        contract.mixinOwner() + "." + contract.handlerMethod()
                                + " must match " + contract.sourceOwner() + "." + contract.sourceMethod());
            }
        }
    }

    @Test
    void locksSourceAnimationsSpellDurationsAndBossBarPrefixCheck() throws Exception {
        try (ZipFile zip = new ZipFile(ContractTestSupport.sourceJar().toFile())) {
            JsonObject animations = JsonParser.parseString(ContractTestSupport.zipText(
                    zip, "assets/darkdoppelganger/animations/doppel_casting_animations.json"
            )).getAsJsonObject().getAsJsonObject("animations");
            Map<String, Double> clips = Map.of(
                    "simple_sword_upward_swipe", 0.45833D,
                    "simple_sword_lunge_stab", 0.41667D,
                    "simple_sword_stab_alternate", 0.45833D,
                    "simple_sword_horizontal_cross_swipe", 0.5D
            );
            clips.forEach((name, duration) -> assertEquals(
                    duration, animations.getAsJsonObject(name).get("animation_length").getAsDouble(), 0.00001D, name
            ));

            assertMethodContainsNumber(zip, "net/bandit/darkdoppelganger/spells/DoppelPortalSpell", "<init>", 14);
            assertMethodContainsNumber(zip, "net/bandit/darkdoppelganger/spells/SummonDoppelMinionSpell", "<init>", 40);

            MethodNode bossBar = method(
                    ContractTestSupport.classNode(zip, "net/bandit/darkdoppelganger/event/ClientEvents").methods,
                    "onCustomizeBossBar",
                    "(Lnet/minecraftforge/client/event/CustomizeGuiOverlayEvent$BossEventProgress;)V"
            );
            assertNotNull(bossBar);
            assertEquals(1, invocationCount(bossBar, "java/lang/String", "startsWith"));
            assertTrue(stringConstants(bossBar).contains("Dark Doppelganger"));
        }
    }

    private static void assertMethodContainsNumber(
            ZipFile zip, String owner, String methodName, int expected
    ) throws Exception {
        MethodNode sourceMethod = ContractTestSupport.classNode(zip, owner).methods.stream()
                .filter(candidate -> candidate.name.equals(methodName))
                .findFirst()
                .orElseThrow();
        Set<Integer> values = new HashSet<>();
        for (AbstractInsnNode instruction : sourceMethod.instructions) {
            int opcode = instruction.getOpcode();
            if (opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5) {
                values.add(opcode - Opcodes.ICONST_0);
            } else if (instruction instanceof org.objectweb.asm.tree.IntInsnNode integer) {
                values.add(integer.operand);
            } else if (instruction instanceof LdcInsnNode literal && literal.cst instanceof Integer integer) {
                values.add(integer);
            }
        }
        assertTrue(values.contains(expected), owner + "." + methodName + " missing " + expected);
    }

    private static MethodNode method(Iterable<MethodNode> methods, String name, String descriptor) {
        for (MethodNode method : methods) {
            if (method.name.equals(name) && method.desc.equals(descriptor)) {
                return method;
            }
        }
        return null;
    }

    private static int componentLiteralCalls(MethodNode method) {
        return invocationCount(method, "net/minecraft/network/chat/Component", "m_237113_");
    }

    private static int invocationCount(MethodNode method, String owner, String name) {
        int count = 0;
        for (AbstractInsnNode instruction : method.instructions) {
            if (instruction instanceof MethodInsnNode call
                    && call.owner.equals(owner)
                    && call.name.equals(name)
                    && (!owner.endsWith("Component") || call.desc.equals(COMPONENT_LITERAL_DESC))) {
                count++;
            }
        }
        return count;
    }

    private static Set<String> stringConstants(MethodNode method) {
        Set<String> values = new HashSet<>();
        for (AbstractInsnNode instruction : method.instructions) {
            if (instruction instanceof LdcInsnNode literal && literal.cst instanceof String value) {
                values.add(value);
            } else if (instruction instanceof InvokeDynamicInsnNode dynamic) {
                Arrays.stream(dynamic.bsmArgs)
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .forEach(values::add);
            }
        }
        return values;
    }

    private record MethodContract(
            String name,
            String descriptor,
            int literalCalls,
            Set<String> literalFragments
    ) {
    }

    private record RedirectHandlerContract(
            String sourceOwner,
            String sourceMethod,
            String sourceDescriptor,
            String mixinOwner,
            String handlerMethod
    ) {
    }
}
