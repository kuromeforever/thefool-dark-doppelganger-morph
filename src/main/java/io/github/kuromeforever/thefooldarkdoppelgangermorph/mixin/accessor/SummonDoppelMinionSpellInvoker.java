package io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.accessor;

import net.bandit.darkdoppelganger.spells.SummonDoppelMinionSpell;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Exact 9.8.2 bridge; the private descriptor is locked by contract tests. */
@Mixin(value = SummonDoppelMinionSpell.class, remap = false)
public interface SummonDoppelMinionSpellInvoker {
    @Invoker("hasLivingMinion")
    static boolean thefoolDarkDoppelgangerMorph$hasLivingMinion(
            ServerLevel level,
            ServerPlayer player
    ) {
        throw new AssertionError("Mixin invoker was not transformed");
    }
}
