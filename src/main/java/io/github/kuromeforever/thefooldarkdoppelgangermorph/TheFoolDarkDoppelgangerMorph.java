package io.github.kuromeforever.thefooldarkdoppelgangermorph;

import com.mojang.logging.LogUtils;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.lifecycle.DarkDoppelgangerLifecycle;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.morph.DarkDoppelgangerMorphRegistration;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.network.DarkDoppelgangerNetwork;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(TheFoolDarkDoppelgangerMorph.MOD_ID)
public final class TheFoolDarkDoppelgangerMorph {
    public static final String MOD_ID = "thefool_dark_doppelganger_morph";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TheFoolDarkDoppelgangerMorph() {
        DarkDoppelgangerNetwork.initialize();
        DarkDoppelgangerLifecycle.initialize();
        DarkDoppelgangerMorphRegistration.bootstrap();
    }
}
