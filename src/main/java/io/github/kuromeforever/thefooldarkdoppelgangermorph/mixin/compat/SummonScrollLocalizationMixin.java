package io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.compat;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.compat.DarkDoppelgangerSourceTranslations;
import net.bandit.darkdoppelganger.item.SummonScrollItem;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SummonScrollItem.class, remap = false)
public abstract class SummonScrollLocalizationMixin {
    @Redirect(
            method = "m_6225_",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;m_237113_(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"),
            require = 1,
            remap = false
    )
    private static MutableComponent thefoolDarkDoppelgangerMorph$translateUse(String original) {
        return DarkDoppelgangerSourceTranslations.translateLiteral(original);
    }

    @Redirect(
            method = "m_7373_",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;m_237113_(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"),
            require = 2,
            remap = false
    )
    private static MutableComponent thefoolDarkDoppelgangerMorph$translateTooltip(String original) {
        return DarkDoppelgangerSourceTranslations.translateLiteral(original);
    }
}
