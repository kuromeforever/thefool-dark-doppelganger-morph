package io.github.kuromeforever.thefooldarkdoppelgangermorph.client;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.network.ClientboundDarkDoppelgangerAction;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.network.DarkDoppelgangerAction;
import liushuangwuyin.bettermorph.api.PlayerIdentity;
import net.bandit.darkdoppelganger.entity.DarkDoppelgangerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Client-only visual projection; no business result is derived from this state. */
public final class DarkDoppelgangerClientActions {
    private static final Map<UUID, ClientAction> ACTIVE = new HashMap<>();

    private DarkDoppelgangerClientActions() {
    }

    public static void handle(ClientboundDarkDoppelgangerAction message) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || !DarkDoppelgangerAction.isKnown(message.actionId())) {
            return;
        }
        ClientAction current = ACTIVE.get(message.playerId());
        if (!message.active()) {
            if (current != null && current.castId().equals(message.castId())) {
                ACTIVE.remove(message.playerId());
                play(message.playerId(), "blank");
            }
            return;
        }
        int duration = Math.min(60, Math.max(1, message.durationTicks()));
        ACTIVE.put(message.playerId(), new ClientAction(
                message.actionId(), message.castId(), minecraft.level.getGameTime() + duration
        ));
        play(message.playerId(), DarkDoppelgangerAction.animation(message.actionId()));
    }

    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            clearAll();
            return;
        }
        long now = minecraft.level.getGameTime();
        for (Map.Entry<UUID, ClientAction> entry : new ArrayList<>(ACTIVE.entrySet())) {
            if (now >= entry.getValue().expiresAtGameTime()
                    && ACTIVE.remove(entry.getKey(), entry.getValue())) {
                play(entry.getKey(), "blank");
            }
        }
    }

    public static void clearPlayer(UUID playerId) {
        if (playerId != null && ACTIVE.remove(playerId) != null) {
            play(playerId, "blank");
        }
    }

    public static void clearAll() {
        ACTIVE.clear();
    }

    private static void play(UUID playerId, String animation) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || animation == null) {
            return;
        }
        Player player = minecraft.level.getPlayerByUUID(playerId);
        if (player == null) {
            return;
        }
        LivingEntity identity = PlayerIdentity.getIdentity(player);
        if (identity instanceof DarkDoppelgangerEntity doppelganger) {
            doppelganger.playAnimation(animation);
        }
    }

    private record ClientAction(
            net.minecraft.resources.ResourceLocation actionId,
            UUID castId,
            long expiresAtGameTime
    ) {
    }
}
