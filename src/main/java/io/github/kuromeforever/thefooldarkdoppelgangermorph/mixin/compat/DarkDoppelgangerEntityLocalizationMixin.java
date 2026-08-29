package io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.compat;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.compat.DarkDoppelgangerSourceTranslations;
import net.bandit.darkdoppelganger.entity.DarkDoppelgangerEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = DarkDoppelgangerEntity.class, remap = false)
public abstract class DarkDoppelgangerEntityLocalizationMixin {
    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    remap = true
            ),
            require = 2,
            remap = false
    )
    private MutableComponent thefoolDarkDoppelgangerMorph$translateConstructor(String original) {
        return DarkDoppelgangerSourceTranslations.translateLiteral(original);
    }

    @Redirect(
            method = "m_8119_",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;m_237113_(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"),
            require = 1,
            remap = false
    )
    private MutableComponent thefoolDarkDoppelgangerMorph$translateTick(String original) {
        return DarkDoppelgangerSourceTranslations.translateLiteral(original);
    }

    @Redirect(
            method = "triggerSecondPhase",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    remap = true
            ),
            require = 1,
            remap = false
    )
    private MutableComponent thefoolDarkDoppelgangerMorph$translateSecondPhase(String original) {
        return DarkDoppelgangerSourceTranslations.translateLiteral(original);
    }

    @Redirect(
            method = "triggerThirdPhase",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    remap = true
            ),
            require = 2,
            remap = false
    )
    private MutableComponent thefoolDarkDoppelgangerMorph$translateThirdPhase(String original) {
        return DarkDoppelgangerSourceTranslations.translateLiteral(original);
    }

    @Redirect(
            method = "summonMinions",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    remap = true
            ),
            require = 2,
            remap = false
    )
    private MutableComponent thefoolDarkDoppelgangerMorph$translateBossMinions(String original) {
        return DarkDoppelgangerSourceTranslations.translateLiteral(original);
    }

    @Redirect(
            method = "m_6667_",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;m_237113_(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"),
            require = 1,
            remap = false
    )
    private MutableComponent thefoolDarkDoppelgangerMorph$translateDeath(String original) {
        return DarkDoppelgangerSourceTranslations.translateLiteral(original);
    }
}
