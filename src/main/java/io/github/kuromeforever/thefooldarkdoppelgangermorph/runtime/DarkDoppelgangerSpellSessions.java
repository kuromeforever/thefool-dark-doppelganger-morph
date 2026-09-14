package io.github.kuromeforever.thefooldarkdoppelgangermorph.runtime;

import com.kurome.ageofmythology.bettermorph.impl.common.MorphSpellCastCoordinator;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import liushuangwuyin.bettermorph.api.PlayerIdentity;
import net.bandit.darkdoppelganger.entity.DarkDoppelgangerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Only receipts produced by this addon's successful native start may own a completion. */
public final class DarkDoppelgangerSpellSessions {
    private static final Map<UUID, Receipt> ACTIVE = new HashMap<>();
    private DarkDoppelgangerSpellSessions() {}
    public static boolean canBegin(ServerPlayer player, AbstractSpell spell) {
        if (!(PlayerIdentity.getIdentity(player) instanceof DarkDoppelgangerEntity)
                || !MorphSpellCastCoordinator.canActivate(player)
                || MagicData.getPlayerMagicData(player).getAdditionalCastData() != null) return false;
        MagicData probe = new MagicData(true);
        try {
            return spell.checkPreCastConditions(player.level(), 1, player, probe);
        } finally {
            probe.resetCastingState();
        }
    }
    public static boolean begin(ServerPlayer player, AbstractSpell spell) {
        if (!(PlayerIdentity.getIdentity(player) instanceof DarkDoppelgangerEntity carrier)) return false;
        if (!MorphSpellCastCoordinator.cast(player.level(), player, spell, 1,
                MorphSpellCastCoordinator.ExecutionMode.NATIVE_SESSION)) return false;
        MagicData data = MagicData.getPlayerMagicData(player);
        var nativeSession = MorphSpellCastCoordinator.captureNativeSession(player, spell);
        if (nativeSession == null) return true;
        ACTIVE.put(player.getUUID(), new Receipt(player, carrier, data, spell,
                data.getCastDuration(), data.getCastDurationRemaining(), nativeSession));
        return true;
    }
    public static Receipt completing(AbstractSpell spell, net.minecraft.world.entity.LivingEntity caster, MagicData data) {
        if (!(caster instanceof ServerPlayer player)) return null;
        Receipt receipt = ACTIVE.get(player.getUUID());
        return receipt != null && receipt.spell == spell && receipt.data == data && receipt.matches() ? receipt : null;
    }
    public static void completed(Receipt receipt, boolean interrupted) {
        if (receipt == null || !ACTIVE.remove(receipt.player.getUUID(), receipt)) return;
        // Completion is business-only; no start, channel, instant or finish pose is projected.
    }

    public static void observeForeignStart(ServerPlayer player) {
        ACTIVE.remove(player.getUUID());
    }
    public static void tick(MinecraftServer server) {
        for (Receipt receipt : new ArrayList<>(ACTIVE.values())) {
            if (!receipt.matches()) {
                ACTIVE.remove(receipt.player.getUUID(), receipt);
            } else receipt.lastRemaining = receipt.data.getCastDurationRemaining();
        }
    }
    public static void clear(ServerPlayer player) {
        Receipt receipt = ACTIVE.remove(player.getUUID());
        if (receipt == null) return;
        MorphSpellCastCoordinator.cancelNativeSession(player, receipt.nativeSession);
    }
    public static void clearAll() { ACTIVE.clear(); }
    public static final class Receipt {
        final ServerPlayer player;
        final DarkDoppelgangerEntity carrier;
        final MagicData data;
        final AbstractSpell spell;
        final int duration;
        final long startedAt;
        int lastRemaining;
        final MorphSpellCastCoordinator.NativeSession nativeSession;
        Receipt(ServerPlayer player, DarkDoppelgangerEntity carrier, MagicData data, AbstractSpell spell,
                int duration, int remaining, MorphSpellCastCoordinator.NativeSession nativeSession) {
            this.player = player; this.carrier = carrier; this.data = data; this.spell = spell;
            this.duration = duration; this.lastRemaining = remaining;
            this.startedAt = player.level().getGameTime();
            this.nativeSession = nativeSession;
        }
        boolean matchesNativeState() {
            if (!MorphSpellCastCoordinator.isNativeSessionActive(player, nativeSession) || !data.isCasting()) return false;
            return ActionPhaseClock.owns(carrier, carrier, data, MagicData.getPlayerMagicData(player),
                    spell, io.redspace.ironsspellbooks.api.registry.SpellRegistry.getSpell(data.getCastingSpellId()),
                    duration, data.getCastDuration(), lastRemaining, data.getCastDurationRemaining(),
                    data.isCasting(), data.getCastingSpellLevel() == 1 && data.getCastSource() == CastSource.NONE);
        }
        boolean matches() {
            return player.isAlive() && player.level() == carrier.level()
                    && PlayerIdentity.getIdentity(player) == carrier && matchesNativeState();
        }
    }
}
