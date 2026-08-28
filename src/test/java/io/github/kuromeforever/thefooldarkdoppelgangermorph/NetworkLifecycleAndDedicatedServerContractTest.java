package io.github.kuromeforever.thefooldarkdoppelgangermorph;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkLifecycleAndDedicatedServerContractTest {
    @Test
    void actionProtocolUsesStableIdsCastUuidAndNoPerTickPackets() throws Exception {
        String actions = ContractTestSupport.source(
                "src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph/network/DarkDoppelgangerAction.java"
        );
        assertEquals(8, count(actions, "public static final ResourceLocation "));
        assertEquals(8, count(actions, "id(\""));

        String message = ContractTestSupport.source(
                "src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph/network/ClientboundDarkDoppelgangerAction.java"
        );
        for (String field : Set.of(
                "UUID playerId", "ResourceLocation actionId", "boolean active",
                "int durationTicks", "UUID castId"
        )) {
            assertTrue(message.contains(field), field);
        }
        assertTrue(message.contains("NetworkDirection.PLAY_TO_CLIENT") || ContractTestSupport.source(
                "src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph/network/DarkDoppelgangerNetwork.java"
        ).contains("NetworkDirection.PLAY_TO_CLIENT"));

        String sessions = ContractTestSupport.source(
                "src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph/network/DarkDoppelgangerActionSessions.java"
        );
        assertTrue(sessions.contains("MAX_ACTION_DURATION_TICKS = 60"));
        assertTrue(sessions.contains("stop(player);"));
        assertTrue(sessions.contains("syncToTracking"));
        assertTrue(sessions.contains("remaining > 0"));
        assertEquals(0, count(sessions.substring(sessions.indexOf("public static void tick")), "true, bounded"));
    }

    @Test
    void clientAnimationStateNeverDeterminesDamageOrCooldown() throws Exception {
        String client = ContractTestSupport.source(
                "src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph/client/DarkDoppelgangerClientActions.java"
        );
        assertTrue(client.contains("Math.min(60, Math.max(1, message.durationTicks()))"));
        assertTrue(client.contains("current.castId().equals(message.castId())"));
        assertTrue(client.contains("PlayerIdentity.getIdentity(player)"));
        assertTrue(client.contains("doppelganger.playAnimation(animation)"));
        for (String forbidden : Set.of(".hurt(", ".heal(", "setCooldown", "sendToServer", "addFreshEntity")) {
            assertFalse(client.contains(forbidden), forbidden);
        }
    }

    @Test
    void lifecycleClearsEveryRequiredBoundaryIdempotently() throws Exception {
        String lifecycle = ContractTestSupport.source(
                "src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph/lifecycle/DarkDoppelgangerLifecycle.java"
        );
        for (String hook : Set.of(
                "IdentitySwapCallback", "IdentityChangedCallback", "LivingDeathEvent", "PlayerEvent.Clone",
                "PlayerLoggedOutEvent", "PlayerChangedDimensionEvent", "LevelEvent.Unload", "ServerStoppingEvent"
        )) {
            assertTrue(lifecycle.contains(hook), hook);
        }
        assertTrue(lifecycle.contains("MorphSpellCastCoordinator.cancel(serverPlayer)"));
        assertTrue(lifecycle.contains("DarkDoppelgangerActionSessions.clearAll()"));
        assertTrue(lifecycle.contains("BladeComboService.clearAll()"));
    }

    @Test
    void mixinPartitionAndSourceSetKeepMinecraftClientReferencesOutOfCommonPackages() throws Exception {
        JsonObject config = JsonParser.parseString(ContractTestSupport.source(
                "src/main/resources/thefool_dark_doppelganger_morph.mixins.json"
        )).getAsJsonObject();
        JsonArray common = config.getAsJsonArray("mixins");
        JsonArray client = config.getAsJsonArray("client");
        assertEquals(6, common.size());
        assertEquals(1, client.size());
        assertTrue(client.toString().contains("client.DarkDoppelgangerBossBarMixin"));
        assertFalse(common.toString().contains("client."));

        try (var paths = Files.walk(ContractTestSupport.projectRoot().resolve(
                "src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph"
        ))) {
            for (var path : paths.filter(candidate -> candidate.toString().endsWith(".java")).toList()) {
                String normalized = path.toString().replace('\\', '/');
                if (normalized.contains("/client/")) {
                    continue;
                }
                String source = Files.readString(path);
                assertFalse(source.contains("import net.minecraft.client."), normalized);
            }
        }
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
