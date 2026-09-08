package io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.client;

import com.kurome.ageofmythology.bettermorph.morph_screen.MorphPreviewRenderGuard;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.client.DarkDoppelgangerPresentation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MorphPreviewRenderGuard.class, remap = false)
public abstract class DarkDoppelgangerPreviewMixin {
    @Inject(method = "begin", at = @At("HEAD"))
    private static void doppel$prepareExactPreview(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        DarkDoppelgangerPresentation.preparePreview(entity);
    }
}
