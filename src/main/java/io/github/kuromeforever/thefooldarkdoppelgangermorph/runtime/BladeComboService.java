package io.github.kuromeforever.thefooldarkdoppelgangermorph.runtime;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.network.DarkDoppelgangerAction;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.network.DarkDoppelgangerActionSessions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Per-player state for one bounded mirror-blade Ability timeline. */
public final class BladeComboService {
    public static final int TIMELINE_TICKS = 22;
    private static final Map<UUID, Session> SESSIONS = new HashMap<>();
    private static final BladeForm[] FORMS = BladeForm.values();

    private BladeComboService() {
    }

    public static boolean begin(ServerPlayer player, LivingEntity frozenTarget) {
        if (player == null || frozenTarget == null || !DarkDoppelgangerCombat.isValidTarget(player, frozenTarget)) {
            return false;
        }
        BladeForm first = FORMS[player.getRandom().nextInt(FORMS.length)];
        BladeForm second = null;
        if (player.getRandom().nextFloat() < 0.4F) {
            int offset = 1 + player.getRandom().nextInt(FORMS.length - 1);
            second = FORMS[(first.ordinal() + offset) % FORMS.length];
        }
        SESSIONS.put(player.getUUID(), new Session(frozenTarget.getUUID(), first, second));
        return true;
    }

    public static void tick(ServerPlayer player, int age) {
        Session session = SESSIONS.get(player.getUUID());
        if (session == null) {
            return;
        }
        if (age == session.first().hitTick()) {
            hit(player, session.targetId(), 12.0F);
        }
        if (session.second() == null) {
            return;
        }
        int secondStart = session.first().durationTicks() + 1;
        if (age == secondStart + session.second().hitTick() - 1) {
            hit(player, session.targetId(), 8.0F);
        }
    }

    public static void clear(ServerPlayer player) {
        if (player != null) {
            SESSIONS.remove(player.getUUID());
            DarkDoppelgangerActionSessions.stop(player);
        }
    }

    public static void clear(UUID playerId) {
        if (playerId != null) {
            SESSIONS.remove(playerId);
        }
    }

    public static void clearAll() {
        SESSIONS.clear();
    }

    private static void hit(ServerPlayer player, UUID targetId, float damage) {
        Entity target = player.serverLevel().getEntity(targetId);
        if (target instanceof LivingEntity living) {
            DarkDoppelgangerCombat.hurtFrozenTarget(player, living, 4.0D, 1.0D, damage, 0.4D);
        }
    }

    public enum BladeForm {
        UPWARD(DarkDoppelgangerAction.BLADE_UPWARD, 9, 5),
        LUNGE(DarkDoppelgangerAction.BLADE_LUNGE, 8, 6),
        STAB(DarkDoppelgangerAction.BLADE_STAB, 10, 8),
        CROSS(DarkDoppelgangerAction.BLADE_CROSS, 10, 8);

        private final net.minecraft.resources.ResourceLocation actionId;
        private final int durationTicks;
        private final int hitTick;

        BladeForm(net.minecraft.resources.ResourceLocation actionId, int durationTicks, int hitTick) {
            this.actionId = actionId;
            this.durationTicks = durationTicks;
            this.hitTick = hitTick;
        }

        public net.minecraft.resources.ResourceLocation actionId() {
            return actionId;
        }

        public int durationTicks() {
            return durationTicks;
        }

        public int hitTick() {
            return hitTick;
        }
    }

    private record Session(UUID targetId, BladeForm first, BladeForm second) {
    }
}
