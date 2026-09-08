package io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.accessor;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Accessed only on a claimed Doppel projection while its source cast pose is active. */
@Mixin(value = AbstractSpellCastingMob.class, remap = false)
public interface DarkDoppelgangerCastPoseAccess {
    @Accessor("animatingLegs") boolean doppel$getAnimatingLegs();
    @Accessor("animatingLegs") void doppel$setAnimatingLegs(boolean value);
}
