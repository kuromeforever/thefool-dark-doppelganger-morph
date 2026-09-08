package io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.client;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.client.DarkDoppelgangerPresentation;
import net.bandit.darkdoppelganger.entity.DarkDoppelgangerEntity;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.model.GeoModel;

/** Reads the source's baked animation. Never replaces a cache entry or a real boss resource. */
@Mixin(value = GeoModel.class, remap = false)
public abstract class DarkDoppelgangerTravelAnimationMixin {
    private static final ResourceLocation DOPPEL_TRAVEL_ANIMATIONS = ResourceLocation.fromNamespaceAndPath(
            "traveloptics", "animations/casting_animations.json");
    private static final java.util.Set<String> DOPPEL_TRAVEL_CLIPS = java.util.Set.of(
            "miasma_start", "miasma_end", "spectral_blink", "tidal_grasp", "tidal_grasp_smack");
    @Inject(method = "getAnimation", at = @At("HEAD"), cancellable = true)
    private void doppel$readOwnedSourceAnimation(GeoAnimatable animatable, String name,
            CallbackInfoReturnable<Animation> cir) {
        if (!(animatable instanceof DarkDoppelgangerEntity entity)
                || !DOPPEL_TRAVEL_CLIPS.contains(name) || !DarkDoppelgangerPresentation.isProjection(entity)) return;
        var source = GeckoLibCache.getBakedAnimations().get(DOPPEL_TRAVEL_ANIMATIONS);
        if (source != null && source.getAnimation(name) != null) cir.setReturnValue(source.getAnimation(name));
    }
}
