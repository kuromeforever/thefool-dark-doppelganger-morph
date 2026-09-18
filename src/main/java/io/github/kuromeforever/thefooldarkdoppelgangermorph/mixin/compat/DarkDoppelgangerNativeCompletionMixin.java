package io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.compat;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.runtime.DarkDoppelgangerSpellSessions;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import net.minecraft.server.level.ServerPlayer;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Observes the two native completion sites without changing any source call or result. */
@Mixin(value = MagicManager.class, remap = false)
public abstract class DarkDoppelgangerNativeCompletionMixin {
    @Redirect(method = "lambda$tick$0", at = @At(value = "INVOKE",
            target = "Lio/redspace/ironsspellbooks/api/spells/AbstractSpell;castSpell(Lnet/minecraft/world/level/Level;ILnet/minecraft/server/level/ServerPlayer;Lio/redspace/ironsspellbooks/api/spells/CastSource;Z)V"),
            require = 3, expect = 3)
    private void doppel$castWithSource(AbstractSpell spell, Level level, int spellLevel,
            ServerPlayer player, CastSource castSource, boolean triggerCooldown) {
        DarkDoppelgangerSpellSessions.runNative(spell, player, MagicData.getPlayerMagicData(player),
                () -> spell.castSpell(level, spellLevel, player, castSource, triggerCooldown));
    }

    @Redirect(method = "lambda$tick$0", at = @At(value = "INVOKE",
            target = "Lio/redspace/ironsspellbooks/api/spells/AbstractSpell;onServerCastTick(Lnet/minecraft/world/level/Level;ILnet/minecraft/world/entity/LivingEntity;Lio/redspace/ironsspellbooks/api/magic/MagicData;)V"),
            require = 1, expect = 1)
    private void doppel$tickWithSource(AbstractSpell spell, Level level, int spellLevel,
            LivingEntity caster, MagicData data) {
        DarkDoppelgangerSpellSessions.runNative(spell, caster, data,
                () -> spell.onServerCastTick(level, spellLevel, caster, data));
    }
    @Redirect(method = "lambda$tick$0", at = @At(value = "INVOKE",
            target = "Lio/redspace/ironsspellbooks/api/spells/AbstractSpell;onServerCastComplete(Lnet/minecraft/world/level/Level;ILnet/minecraft/world/entity/LivingEntity;Lio/redspace/ironsspellbooks/api/magic/MagicData;Z)V"),
            require = 2, expect = 2)
    private void doppel$observeCompletion(AbstractSpell spell, Level level, int spellLevel,
            LivingEntity caster, MagicData data, boolean interrupted) {
        var receipt = DarkDoppelgangerSpellSessions.completing(spell, caster, data);
        boolean returned = false;
        try {
            DarkDoppelgangerSpellSessions.runNative(spell, caster, data,
                    () -> spell.onServerCastComplete(level, spellLevel, caster, data, interrupted));
            returned = true;
        } finally {
            DarkDoppelgangerSpellSessions.completed(receipt, interrupted || !returned);
        }
    }
}
