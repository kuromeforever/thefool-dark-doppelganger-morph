package io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.compat;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.compat.DarkDoppelgangerSourceTranslations;
import net.bandit.darkdoppelganger.entity.DarkDoppelgangerMinionEntity;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = DarkDoppelgangerMinionEntity.class, remap = false)
public abstract class DarkDoppelgangerMinionLocalizationMixin {
    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;",
                    remap = true
            ),
            require = 1,
            remap = false
    )
    private static MutableComponent thefoolDarkDoppelgangerMorph$translateName(String original) {
        return DarkDoppelgangerSourceTranslations.translateLiteral(original);
    }
}
