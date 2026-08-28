package io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.client;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.compat.DarkDoppelgangerSourceTranslations;
import net.bandit.darkdoppelganger.event.ClientEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ClientEvents.class, remap = false)
public abstract class DarkDoppelgangerBossBarMixin {
    @Redirect(
            method = "onCustomizeBossBar",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;startsWith(Ljava/lang/String;)Z"),
            require = 1,
            remap = false
    )
    private static boolean thefoolDarkDoppelgangerMorph$acceptTranslatedName(
            String renderedName,
            String ignoredEnglishPrefix
    ) {
        return DarkDoppelgangerSourceTranslations.isTranslatedBossBarName(renderedName);
    }
}
