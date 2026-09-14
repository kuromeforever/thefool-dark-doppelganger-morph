package io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.client;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.client.DarkDoppelgangerClientActions;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.client.DarkDoppelgangerPresentation;
import net.bandit.darkdoppelganger.entity.DarkDoppelgangerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

/** Only detached, authenticated morph/preview carriers omit source action poses. */
@Mixin(value = DarkDoppelgangerEntity.class, remap = false)
public abstract class DarkDoppelgangerNoCastAnimationMixin {
    @Inject(method = "predicate", at = @At("HEAD"), cancellable = true)
    private void doppel$omitQueuedAction(AnimationState<DarkDoppelgangerEntity> state,
                                       CallbackInfoReturnable<PlayState> cir) {
        var entity = (DarkDoppelgangerEntity) (Object) this;
        if (!DarkDoppelgangerPresentation.isProjection(entity)) return;
        DarkDoppelgangerClientActions.suppressPose(entity);
        cir.setReturnValue(PlayState.STOP);
    }

    @Inject(method = "isAnimating", at = @At("HEAD"), cancellable = true)
    private void doppel$keepLocomotion(CallbackInfoReturnable<Boolean> cir) {
        if (DarkDoppelgangerPresentation.isProjection((DarkDoppelgangerEntity) (Object) this))
            cir.setReturnValue(false);
    }
}
