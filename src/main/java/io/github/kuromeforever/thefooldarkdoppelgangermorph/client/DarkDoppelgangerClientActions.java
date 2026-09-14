package io.github.kuromeforever.thefooldarkdoppelgangermorph.client;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.accessor.DarkDoppelgangerPresentationAccess;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.accessor.DarkDoppelgangerCastPoseAccess;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.morph.DarkDoppelgangerSpellCatalog;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.network.ClientboundDarkDoppelgangerAction;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.network.DarkDoppelgangerAction;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.runtime.ActionPhaseClock;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.CastType;
import liushuangwuyin.bettermorph.api.PlayerIdentity;
import net.bandit.darkdoppelganger.entity.DarkDoppelgangerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

/** Client-only phase projection. A pending START waits for its exact synchronized carrier. */
public final class DarkDoppelgangerClientActions {
    private static final Map<UUID, ClientAction> ACTIVE = new HashMap<>();
    private static final Map<UUID, Long> LAST_SEQUENCE = new HashMap<>();
    private static final Map<AnimationController<?>, ClientAction> CONTROLLERS = new IdentityHashMap<>();
    private DarkDoppelgangerClientActions() {}

    public static void handle(ClientboundDarkDoppelgangerAction message) {
        var client = Minecraft.getInstance();
        if (client.level == null || !DarkDoppelgangerAction.isKnown(message.actionId())
                || !client.level.dimension().location().equals(message.dimension())) return;
        ClientAction current = ACTIVE.get(message.playerId());
        if (!message.active()) {
            // A STOP never clears a newer cast, even when both use the same spell and carrier.
            if (current != null && ActionPhaseClock.acceptsStop(current.message.castId(), message.castId())) {
                ACTIVE.remove(message.playerId());
                stop(current);
            }
            return;
        }
        if (!ActionPhaseClock.acceptsStart(message.sequence(), LAST_SEQUENCE.getOrDefault(message.playerId(), -1L),
                message.startedAt(), client.level.getGameTime(), message.durationTicks())) return;
        LAST_SEQUENCE.put(message.playerId(), message.sequence());
        if (current != null) stop(current);
        ACTIVE.put(message.playerId(), new ClientAction(message));
        Player player = client.level.getPlayerByUUID(message.playerId());
        if (player != null && PlayerIdentity.getIdentity(player) instanceof DarkDoppelgangerEntity identity)
            applyPending(player, identity);
    }

    public static void applyPending(Player player, DarkDoppelgangerEntity identity) {
        ClientAction action = ACTIVE.get(player.getUUID());
        if (action == null) return;
        if (!phaseActive(action, player.level())) {
            clearPlayer(player.getUUID());
            return;
        }
        if (!ActionPhaseClock.carrierMatches(identity, PlayerIdentity.getIdentity(player),
                action.message.carrierId(), identity.getUUID())) {
            // readNbt may replace the server UUID on the same client object. Retire its old cast.
            // An unbound early START instead waits until the matching NBT has been installed.
            if (action.carrier != null) clearPlayer(player.getUUID());
            return;
        }
        if (action.carrier == identity) return;
        // A rebuilt entity with the same synchronized carrier UUID is re-bound by exact object.
        if (action.carrier != null) stop(action);
        DarkDoppelgangerPresentation.prepareIdentity(player, identity);
        var access = (DarkDoppelgangerPresentationAccess) identity;
        RawAnimation raw = animation(action.message);
        action.carrier = identity;
        var pose = (DarkDoppelgangerCastPoseAccess) identity;
        action.previousAnimatingLegs = pose.doppel$getAnimatingLegs();
        if (DarkDoppelgangerSpellCatalog.contains(action.message.actionId())) {
            var spell = SpellRegistry.getSpell(action.message.actionId().toString());
            var holder = action.message.finishing() ? spell.getCastFinishAnimation() : spell.getCastStartAnimation();
            if (holder.getForMob().isEmpty() && spell.getCastType() == CastType.INSTANT) holder = spell.getCastStartAnimation();
            pose.doppel$setAnimatingLegs(holder.animatesLegs);
        }
        action.controller = access.doppel$getActionController();
        action.speed = speed(action.message);
        action.controller.forceAnimationReset();
        action.controller.setAnimationSpeed(action.speed);
        access.doppel$setQueuedAnimation(raw == null ? RawAnimation.begin().thenPlay("blank") : raw);
        if (raw != null) CONTROLLERS.put(action.controller, action);
    }

    private static RawAnimation animation(ClientboundDarkDoppelgangerAction message) {
        if (!DarkDoppelgangerSpellCatalog.contains(message.actionId())) {
            String name = DarkDoppelgangerAction.animation(message.actionId());
            return name == null ? null : RawAnimation.begin().thenPlay(name);
        }
        var spell = SpellRegistry.getSpell(message.actionId().toString());
        if (!message.finishing()) return spell.getCastStartAnimation().getForMob().orElse(null);
        return spell.getCastFinishAnimation().getForMob().orElseGet(() ->
                spell.getCastType() == CastType.INSTANT ? spell.getCastStartAnimation().getForMob().orElse(null) : null);
    }
    private static double speed(ClientboundDarkDoppelgangerAction message) {
        if (message.actionId().equals(DarkDoppelgangerAction.LIFE_DRAIN))
            return 1.6 / Math.max(1, message.durationTicks());
        if (!message.finishing() && DarkDoppelgangerSpellCatalog.contains(message.actionId()) && message.castDuration() > 0)
            return Math.max(0.01, (double) SpellRegistry.getSpell(message.actionId().toString()).getCastTime(1) / message.castDuration());
        return 1;
    }
    /** Called only for an exact controller claimed above. Native/world controllers are untouched. */
    public static double animationTick(AnimationController<?> controller, double sourceTick, double original) {
        ClientAction action = CONTROLLERS.get(controller);
        var level = Minecraft.getInstance().level;
        if (action == null || level == null || action.carrier == null) return original;
        Player player = level.getPlayerByUUID(action.message.playerId());
        if (player == null || !phaseActive(action, level)
                || !ActionPhaseClock.carrierMatches(action.carrier, PlayerIdentity.getIdentity(player),
                action.message.carrierId(), action.carrier.getUUID())) return original;
        return ActionPhaseClock.animationTick(action.message.startedAt(), level.getGameTime(), sourceTick, action.speed);
    }
    public static void tick() {
        var client = Minecraft.getInstance();
        if (client.level == null) { clearAll(); return; }
        for (var entry : new ArrayList<>(ACTIVE.entrySet())) {
            ClientAction action = entry.getValue();
            if (!phaseActive(action, client.level)) {
                if (ACTIVE.remove(entry.getKey(), action)) stop(action);
                continue;
            }
            Player player = client.level.getPlayerByUUID(entry.getKey());
            if (player != null && PlayerIdentity.getIdentity(player) instanceof DarkDoppelgangerEntity identity)
                applyPending(player, identity);
        }
    }
    public static void clearPlayer(UUID playerId) {
        ClientAction action = ACTIVE.remove(playerId);
        if (action != null) stop(action);
        // Keep sequence tombstones until world logout: a delayed START cannot revive an old cast.
    }
    public static void identityChanging(Player player, net.minecraft.world.entity.LivingEntity next) {
        ClientAction action = ACTIVE.get(player.getUUID());
        if (action == null) return;
        if (!phaseActive(action, player.level())) {
            clearPlayer(player.getUUID());
            return;
        }
        // BetterMorph creates -> setIdentity/callback -> readNbt. Keep unbound packets through
        // this callback and authenticate their server UUID only in the updater/client tick.
        // Rebuilds stop the old controller first and may rebind only after matching NBT arrives.
        if (ActionPhaseClock.releaseBeforeIdentityNbt(action.carrier, next)) stop(action);
    }
    private static boolean phaseActive(ClientAction action, net.minecraft.world.level.Level level) {
        return ActionPhaseClock.phaseActive(action.message.dimension(), level.dimension().location(),
                action.message.startedAt(), level.getGameTime(), action.message.durationTicks());
    }
    public static void clearAll() {
        for (ClientAction action : ACTIVE.values()) stop(action);
        ACTIVE.clear(); CONTROLLERS.clear(); LAST_SEQUENCE.clear();
        DarkDoppelgangerPresentation.clear();
    }
    private static void stop(ClientAction action) {
        if (action.controller != null) CONTROLLERS.remove(action.controller);
        if (action.carrier != null) {
            ((DarkDoppelgangerPresentationAccess) action.carrier).doppel$setQueuedAnimation(null);
            ((DarkDoppelgangerCastPoseAccess) action.carrier).doppel$setAnimatingLegs(action.previousAnimatingLegs);
            if (action.controller != null) {
                action.controller.stop();
                action.controller.forceAnimationReset();
                action.controller.setAnimationSpeed(1);
            }
        }
        action.carrier = null; action.controller = null;
    }
    private static final class ClientAction {
        final ClientboundDarkDoppelgangerAction message;
        DarkDoppelgangerEntity carrier;
        AnimationController<?> controller;
        double speed = 1;
        boolean previousAnimatingLegs;
        ClientAction(ClientboundDarkDoppelgangerAction message) { this.message = message; }
    }
}
