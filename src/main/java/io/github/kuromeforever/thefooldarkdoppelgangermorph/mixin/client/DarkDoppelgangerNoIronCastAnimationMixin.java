package io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.client;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.client.DarkDoppelgangerPresentation;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.bandit.darkdoppelganger.entity.DarkDoppelgangerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

/** Stops inherited visual controllers without changing MagicData or native spell completion. */
@Mixin(value = AbstractSpellCastingMob.class, remap = false)
public abstract class DarkDoppelgangerNoIronCastAnimationMixin {
    @Inject(method = {"instantCastingPredicate", "longCastingPredicate", "otherCastingPredicate"},
            at = @At("HEAD"), cancellable = true)
    private void doppel$omitInheritedCast(AnimationState<?> state, CallbackInfoReturnable<PlayState> cir) {
        if (!((Object) this instanceof DarkDoppelgangerEntity entity)
                || !DarkDoppelgangerPresentation.isProjection(entity)) return;
        var controller = state.getController();
        if (controller.getAnimationState() != AnimationController.State.STOPPED) {
            controller.stop();
            controller.forceAnimationReset();
            controller.setAnimationSpeed(1);
        }
        cir.setReturnValue(PlayState.STOP);
    }

    @Inject(method = "shouldPointArmsWhileCasting", at = @At("HEAD"), cancellable = true)
    private void doppel$omitPointingArms(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof DarkDoppelgangerEntity entity
                && DarkDoppelgangerPresentation.isProjection(entity)) cir.setReturnValue(false);
    }
}
