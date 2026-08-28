package io.github.kuromeforever.thefooldarkdoppelgangermorph.network;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Bounded server-owned visual facts; this map never stores cooldown state. */
public final class DarkDoppelgangerActionSessions {
    public static final int MAX_ACTION_DURATION_TICKS = 60;
    private static final Map<UUID, ActiveAction> ACTIVE = new LinkedHashMap<>();

    private DarkDoppelgangerActionSessions() {
    }

    public static void beginServerSession() {
        ACTIVE.clear();
    }

    public static UUID start(ServerPlayer player, net.minecraft.resources.ResourceLocation actionId, int durationTicks) {
        if (player == null || !DarkDoppelgangerAction.isKnown(actionId)) {
            throw new IllegalArgumentException("Unknown Dark Doppelganger action " + actionId);
        }
        int bounded = Math.min(MAX_ACTION_DURATION_TICKS, Math.max(1, durationTicks));
        stop(player);
        UUID castId = UUID.randomUUID();
        ACTIVE.put(player.getUUID(), new ActiveAction(
                actionId,
                castId,
                player.serverLevel().dimension(),
                player.serverLevel().getGameTime() + bounded
        ));
        DarkDoppelgangerNetwork.broadcast(player, actionId, true, bounded, castId);
        return castId;
    }

    public static void stop(ServerPlayer player) {
        if (player == null) {
            return;
        }
        ActiveAction previous = ACTIVE.remove(player.getUUID());
        if (previous != null) {
            DarkDoppelgangerNetwork.broadcast(player, previous.actionId(), false, 0, previous.castId());
        }
    }

    public static void clearPlayer(MinecraftServer server, UUID playerId) {
        if (playerId == null) {
            return;
        }
        ActiveAction previous = ACTIVE.remove(playerId);
        ServerPlayer player = server == null ? null : server.getPlayerList().getPlayer(playerId);
        if (previous != null && player != null) {
            DarkDoppelgangerNetwork.broadcast(player, previous.actionId(), false, 0, previous.castId());
        }
    }

    public static void syncToTracking(ServerPlayer recipient, ServerPlayer trackedPlayer) {
        if (recipient == null || trackedPlayer == null || recipient.serverLevel() != trackedPlayer.serverLevel()) {
            return;
        }
        ActiveAction active = ACTIVE.get(trackedPlayer.getUUID());
        if (active == null || !active.dimension().equals(trackedPlayer.serverLevel().dimension())) {
            return;
        }
        int remaining = (int)Math.min(
                MAX_ACTION_DURATION_TICKS,
                Math.max(0L, active.expiresAtGameTime() - trackedPlayer.serverLevel().getGameTime())
        );
        if (remaining > 0) {
            DarkDoppelgangerNetwork.sendTo(
                    recipient, trackedPlayer.getUUID(), active.actionId(), remaining, active.castId()
            );
        }
    }

    public static void tick(MinecraftServer server) {
        for (Map.Entry<UUID, ActiveAction> entry : new ArrayList<>(ACTIVE.entrySet())) {
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            ServerLevel level = server.getLevel(entry.getValue().dimension());
            boolean invalid = player == null || !player.isAlive() || player.serverLevel() != level;
            boolean expired = level == null || level.getGameTime() >= entry.getValue().expiresAtGameTime();
            if ((invalid || expired) && ACTIVE.remove(entry.getKey(), entry.getValue()) && player != null) {
                DarkDoppelgangerNetwork.broadcast(
                        player, entry.getValue().actionId(), false, 0, entry.getValue().castId()
                );
            }
        }
    }

    public static void clearLevel(ResourceKey<Level> dimension) {
        ACTIVE.entrySet().removeIf(entry -> entry.getValue().dimension().equals(dimension));
    }

    public static void clearAll() {
        ACTIVE.clear();
    }

    private record ActiveAction(
            net.minecraft.resources.ResourceLocation actionId,
            UUID castId,
            ResourceKey<Level> dimension,
            long expiresAtGameTime
    ) {
    }
}
