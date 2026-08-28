package io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.compat;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.compat.DarkDoppelgangerSourceTranslations;
import net.bandit.darkdoppelganger.command.ModCommands;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ModCommands.class, remap = false)
public abstract class DarkDoppelgangerCommandLocalizationMixin {
    @Redirect(
            method = "killDoppelgangers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    remap = true
            ),
            require = 1,
            remap = false
    )
    private static MutableComponent thefoolDarkDoppelgangerMorph$translateNone(String original) {
        return DarkDoppelgangerSourceTranslations.translateLiteral(original);
    }

    @Redirect(
            method = "lambda$killDoppelgangers$4",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    remap = true
            ),
            require = 1,
            remap = false
    )
    private static MutableComponent thefoolDarkDoppelgangerMorph$translateRemoved(String original) {
        return DarkDoppelgangerSourceTranslations.translateLiteral(original);
    }
}
