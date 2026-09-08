package io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.accessor;

import net.bandit.darkdoppelganger.entity.DarkDoppelgangerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/** Presentation only. Callers must prove an exact identity or independent preview reference. */
@Mixin(value = DarkDoppelgangerEntity.class, remap = false)
public interface DarkDoppelgangerPresentationAccess {
    @Accessor("age") int doppel$getPresentationAge();
    @Accessor("age") void doppel$setPresentationAge(int age);
    @Accessor("spawnController") AnimationController<DarkDoppelgangerEntity> doppel$getSpawnController();
    @Accessor("meleeController") AnimationController<DarkDoppelgangerEntity> doppel$getActionController();
    @Accessor("animationToPlay") void doppel$setQueuedAnimation(RawAnimation animation);
}
