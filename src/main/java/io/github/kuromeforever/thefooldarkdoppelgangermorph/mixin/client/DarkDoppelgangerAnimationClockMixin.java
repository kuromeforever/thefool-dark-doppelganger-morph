package io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.client;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.client.DarkDoppelgangerClientActions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.core.animation.AnimationController;

@Mixin(value = AnimationController.class, remap = false)
public abstract class DarkDoppelgangerAnimationClockMixin {
    @Inject(method = "adjustTick", at = @At("RETURN"), cancellable = true)
    private void doppel$restorePhaseTime(double tick, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(DarkDoppelgangerClientActions.animationTick(
                (AnimationController<?>)(Object)this, tick, cir.getReturnValue()));
    }
}
