package io.github.kuromeforever.thefooldarkdoppelgangermorph.network;

import liushuangwuyin.bettermorph.api.PlayerIdentity;
import net.bandit.darkdoppelganger.entity.DarkDoppelgangerEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Projection lifetimes are independent from the player's only input cooldown. */
public final class DarkDoppelgangerActionSessions {
    public static final int MAX_ACTION_DURATION_TICKS = 6000;
    private static final Map<UUID, ClientboundDarkDoppelgangerAction> ACTIVE = new LinkedHashMap<>();
    private static long sequence;
    private DarkDoppelgangerActionSessions() {}
    public static void beginServerSession() { ACTIVE.clear(); sequence = 0; }
    public static UUID start(ServerPlayer player, ResourceLocation id, int duration) {
        return start(player, id, duration, false, duration);
    }
    public static UUID start(ServerPlayer player, ResourceLocation id, int duration, boolean finishing, int castDuration) {
        return startAt(player, id, duration, finishing, castDuration, player.level().getGameTime());
    }
    public static UUID startAt(ServerPlayer player, ResourceLocation id, int duration, boolean finishing,
            int castDuration, long startedAt) {
        if (player == null || !DarkDoppelgangerAction.isKnown(id)
                || !(PlayerIdentity.getIdentity(player) instanceof DarkDoppelgangerEntity carrier)) {
            throw new IllegalArgumentException("Unowned Dark Doppelganger action " + id);
        }
        stop(player);
        UUID castId = UUID.randomUUID();
        var message = new ClientboundDarkDoppelgangerAction(player.getUUID(), id, true,
                Math.min(MAX_ACTION_DURATION_TICKS, Math.max(1, duration)), castId,
                carrier.getUUID(), player.level().dimension().location(), startedAt,
                ++sequence, finishing, castDuration);
        ACTIVE.put(player.getUUID(), message);
        DarkDoppelgangerNetwork.broadcast(player, message);
        return castId;
    }
    public static void stop(ServerPlayer player) {
        if (player == null) return;
        var previous = ACTIVE.remove(player.getUUID());
        if (previous != null) DarkDoppelgangerNetwork.broadcast(player, previous.stopped());
    }
    public static void stop(ServerPlayer player, UUID expectedCast) {
        var current = ACTIVE.get(player.getUUID());
        if (current != null && current.castId().equals(expectedCast)) stop(player);
    }
    public static void clearPlayer(MinecraftServer server, UUID id) {
        var previous = ACTIVE.remove(id);
        ServerPlayer player = server == null ? null : server.getPlayerList().getPlayer(id);
        if (previous != null && player != null) DarkDoppelgangerNetwork.broadcast(player, previous.stopped());
    }
    public static void syncToTracking(ServerPlayer recipient, ServerPlayer player) {
        if (recipient == null || player == null || recipient.serverLevel() != player.serverLevel()) return;
        var active = ACTIVE.get(player.getUUID());
        if (active != null && valid(player, active)) DarkDoppelgangerNetwork.sendTo(recipient, active);
    }
    public static void tick(MinecraftServer server) {
        for (var entry : new ArrayList<>(ACTIVE.entrySet())) {
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null || !valid(player, entry.getValue())) clearPlayer(server, entry.getKey());
        }
    }
    private static boolean valid(ServerPlayer player, ClientboundDarkDoppelgangerAction action) {
        var carrier = PlayerIdentity.getIdentity(player);
        return player.isAlive() && carrier instanceof DarkDoppelgangerEntity
                && carrier.getUUID().equals(action.carrierId())
                && player.level().dimension().location().equals(action.dimension())
                && action.elapsed(player.level().getGameTime()) < action.durationTicks();
    }
    public static void clearLevel(ResourceKey<Level> dimension) {
        ACTIVE.entrySet().removeIf(entry -> entry.getValue().dimension().equals(dimension.location()));
    }
    public static void clearAll() { ACTIVE.clear(); }
}
