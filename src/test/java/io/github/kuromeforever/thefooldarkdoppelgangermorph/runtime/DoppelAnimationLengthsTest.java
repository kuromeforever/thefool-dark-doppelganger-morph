package io.github.kuromeforever.thefooldarkdoppelgangermorph.runtime;

import org.junit.jupiter.api.Test;
import software.bernie.geckolib.core.animation.RawAnimation;
import static org.junit.jupiter.api.Assertions.*;

class DoppelAnimationLengthsTest {
    @Test void sourceFinishClipReleasesInSixTicksInsteadOfSixSeconds() {
        assertEquals(6, DoppelAnimationLengths.finishTicks(RawAnimation.begin().thenPlay("long_cast_finish")));
        assertEquals(0, DoppelAnimationLengths.finishTicks(null));
        assertEquals(22, DoppelAnimationLengths.finishTicks(RawAnimation.begin().thenPlay("unknown_future_clip")));
    }
    @Test void hostileOrLongAssetCannotHoldRecoveryIndefinitely() {
        assertEquals(120, DoppelAnimationLengths.finishTicks(RawAnimation.begin().thenPlay("charged_throw")));
    }
}
