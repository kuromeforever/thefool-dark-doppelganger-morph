package io.github.kuromeforever.thefooldarkdoppelgangermorph.client;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.accessor.DarkDoppelgangerPresentationAccess;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.accessor.DarkDoppelgangerCastPoseAccess;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.network.ClientboundDarkDoppelgangerAction;
import liushuangwuyin.bettermorph.api.PlayerIdentity;
import net.bandit.darkdoppelganger.entity.DarkDoppelgangerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.core.animation.AnimationController;
import java.util.UUID;

/** Protocol-2 compatibility sink: late action packets can never revive a cast pose. */
public final class DarkDoppelgangerClientActions {
    private DarkDoppelgangerClientActions() {}

    public static void handle(ClientboundDarkDoppelgangerAction message) {
        // Active skills intentionally have no forced animation, including old/late STARTs.
    }

    public static void applyPending(Player player, DarkDoppelgangerEntity identity) {
        if (PlayerIdentity.getIdentity(player) == identity) suppressPose(identity);
    }

    public static void suppressPose(DarkDoppelgangerEntity identity) {
        if (!DarkDoppelgangerPresentation.isProjection(identity)) return;
        var access = (DarkDoppelgangerPresentationAccess) identity;
        access.doppel$setQueuedAnimation(null);
        ((DarkDoppelgangerCastPoseAccess) identity).doppel$setAnimatingLegs(false);
        var controller = access.doppel$getActionController();
        if (controller.getAnimationState() != AnimationController.State.STOPPED) {
            controller.stop();
            controller.forceAnimationReset();
            controller.setAnimationSpeed(1);
        }
    }

    /** Existing GeckoLib hook stays compatible and never owns a controller. */
    public static double animationTick(AnimationController<?> controller, double sourceTick, double original) {
        return original;
    }
    public static void tick() {
        if (Minecraft.getInstance().level == null) clearAll();
    }
    public static void clearPlayer(UUID playerId) {}
    public static void identityChanging(Player player, LivingEntity next) {}
    public static void clearAll() { DarkDoppelgangerPresentation.clear(); }
}
