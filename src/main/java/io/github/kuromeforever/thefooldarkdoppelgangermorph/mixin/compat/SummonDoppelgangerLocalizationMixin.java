package io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.compat;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.compat.DarkDoppelgangerSourceTranslations;
import net.bandit.darkdoppelganger.event.SummonDoppelganger;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SummonDoppelganger.class, remap = false)
public abstract class SummonDoppelgangerLocalizationMixin {
    @Redirect(
            method = "summonDoppelganger",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    remap = true
            ),
            require = 2,
            remap = false
    )
    private static MutableComponent thefoolDarkDoppelgangerMorph$translateCommandResult(String original) {
        return DarkDoppelgangerSourceTranslations.translateLiteral(original);
    }

    @Redirect(
            method = "lambda$summonDoppelganger$1",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    remap = true
            ),
            require = 1,
            remap = false
    )
    private static MutableComponent thefoolDarkDoppelgangerMorph$translateInvalidTarget(String original) {
        return DarkDoppelgangerSourceTranslations.translateLiteral(original);
    }

    @Redirect(
            method = "lambda$summonDoppelganger$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    remap = true
            ),
            require = 1,
            remap = false
    )
    private static MutableComponent thefoolDarkDoppelgangerMorph$translateCountdown(
            String original,
            Player copiedPlayer
    ) {
        return DarkDoppelgangerSourceTranslations.spawnCountdown(copiedPlayer.getDisplayName());
    }
}
