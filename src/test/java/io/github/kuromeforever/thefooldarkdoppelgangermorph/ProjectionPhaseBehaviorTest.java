package io.github.kuromeforever.thefooldarkdoppelgangermorph;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.runtime.ActionPhaseClock;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.client.BirthPoseReset;
import org.junit.jupiter.api.Test;
import software.bernie.geckolib.cache.object.GeoBone;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ProjectionPhaseBehaviorTest {
    @Test void earlyStartSurvivesSetIdentityBeforeReadNbtAndBindsOnlyAfterServerUuidArrives() {
        UUID serverId = UUID.randomUUID(), constructorId = UUID.randomUUID();
        Object createdIdentity = new Object();
        Object bound = null;
        assertTrue(ActionPhaseClock.phaseActive("overworld", "overworld", 100, 101, 40));
        // BetterMorph's callback sees a freshly constructed UUID before applying the packet NBT.
        assertFalse(ActionPhaseClock.releaseBeforeIdentityNbt(bound, createdIdentity));
        assertFalse(ActionPhaseClock.carrierMatches(createdIdentity, createdIdentity, serverId, constructorId));
        // readNbt installs the server UUID on the same object, then updater/tick can bind it.
        assertTrue(ActionPhaseClock.carrierMatches(createdIdentity, createdIdentity, serverId, serverId));
        bound = createdIdentity;
        assertFalse(ActionPhaseClock.releaseBeforeIdentityNbt(bound, createdIdentity));
        assertEquals(1.5, ActionPhaseClock.animationTick(100, 101, .5, 1), .001);
    }
    @Test void readNbtReplacingServerUuidOnSameObjectInvalidatesOldCastAndClock() {
        Object reusedIdentity = new Object();
        UUID oldServerId = UUID.randomUUID(), newServerId = UUID.randomUUID();
        assertTrue(ActionPhaseClock.carrierMatches(reusedIdentity, reusedIdentity, oldServerId, oldServerId));
        assertFalse(ActionPhaseClock.releaseBeforeIdentityNbt(reusedIdentity, reusedIdentity));
        // Reference equality survives NBT replacement, but the previous server carrier does not.
        assertFalse(ActionPhaseClock.carrierMatches(reusedIdentity, reusedIdentity, oldServerId, newServerId));
        assertTrue(ActionPhaseClock.carrierMatches(reusedIdentity, reusedIdentity, newServerId, newServerId));
    }
    @Test void legitimateSameServerCarrierRebuildReleasesOldControllerBeforeRebinding() {
        Object oldIdentity = new Object(), rebuiltIdentity = new Object();
        UUID serverId = UUID.randomUUID();
        assertTrue(ActionPhaseClock.releaseBeforeIdentityNbt(oldIdentity, rebuiltIdentity));
        assertFalse(ActionPhaseClock.carrierMatches(oldIdentity, rebuiltIdentity, serverId, serverId));
        assertTrue(ActionPhaseClock.carrierMatches(rebuiltIdentity, rebuiltIdentity, serverId, serverId));
    }
    @Test void pendingIdentityCannotOutliveItsDimensionOrPhase() {
        assertFalse(ActionPhaseClock.phaseActive("overworld", "nether", 100, 101, 40));
        assertFalse(ActionPhaseClock.phaseActive("overworld", "overworld", 100, 140, 40));
        assertFalse(ActionPhaseClock.phaseActive("overworld", "overworld", 100, 101, 6001));
    }
    @Test void lateObserverStartsAtActualElapsedFrame() {
        assertEquals(27.5, ActionPhaseClock.animationTick(100, 127, 42.5, 1), 0.001);
        assertTrue(ActionPhaseClock.acceptsStart(2, 1, 100, 127, 40));
    }
    @Test void expiredLateStartNeverReplaysCompletedDamageFrames() {
        assertFalse(ActionPhaseClock.acceptsStart(2, 1, 100, 140, 40));
        assertFalse(ActionPhaseClock.acceptsStart(2, 1, 100, 300, 40));
    }
    @Test void duplicateOrReorderedStartCannotRestartNewAnimation() {
        assertFalse(ActionPhaseClock.acceptsStart(3, 3, 100, 101, 40));
        assertFalse(ActionPhaseClock.acceptsStart(2, 3, 100, 101, 40));
        assertTrue(ActionPhaseClock.acceptsStart(4, 3, 100, 101, 40));
    }
    @Test void previousStopCannotCancelNextCastOfSameAction() {
        UUID previous = UUID.randomUUID(), current = UUID.randomUUID();
        assertFalse(ActionPhaseClock.acceptsStop(current, previous));
        assertTrue(ActionPhaseClock.acceptsStop(current, current));
        assertFalse(ActionPhaseClock.acceptsStop(null, previous));
    }
    @Test void castingSpeedPreservesPhaseProgressRatherThanReplayingStart() {
        assertEquals(10.25, ActionPhaseClock.animationTick(100, 120, 1.5, 0.5), 0.001);
    }
    @Test void effectLifetimeIsIndependentFromSixtyTickInputCooldown() {
        assertTrue(ActionPhaseClock.acceptsStart(1, 0, 100, 170, 120));
        assertFalse(ActionPhaseClock.acceptsStart(1, 0, 100, 170, 60));
        assertFalse(ActionPhaseClock.acceptsStart(1, 0, 100, 100, 6001));
    }
    @Test void clockSkewBeforeStartDoesNotSeekToNegativeFrame() {
        assertEquals(0, ActionPhaseClock.elapsed(105, 100));
        assertEquals(.25, ActionPhaseClock.animationTick(105, 100, 9.25, 1), .001);
    }
    @Test void sameSpellDifferentCarrierOrMagicDataIsNeverOwned() {
        Object carrier = new Object(), data = new Object(), spell = new Object();
        assertTrue(ActionPhaseClock.owns(carrier, carrier, data, data, spell, spell, 40, 40, 30, 29, true, true));
        assertFalse(ActionPhaseClock.owns(carrier, new Object(), data, data, spell, spell, 40, 40, 30, 29, true, true));
        assertFalse(ActionPhaseClock.owns(carrier, carrier, data, new Object(), spell, spell, 40, 40, 30, 29, true, true));
        assertFalse(ActionPhaseClock.owns(carrier, carrier, data, data, spell, new Object(), 40, 40, 30, 29, true, true));
        assertFalse(ActionPhaseClock.owns(carrier, carrier, data, data, spell, spell, 40, 40, 30, 40, true, true));
        assertFalse(ActionPhaseClock.owns(carrier, carrier, data, data, spell, spell, 40, 40, 30, 29, true, false));
    }
    @Test void cachedZeroThicknessBirthBoneRestoresAuthoredPose() {
        GeoBone body = new GeoBone(null, "body", false, 0.0, false, false);
        body.setScaleX(1.5F); body.setScaleY(2); body.setScaleZ(.75F);
        body.setPosX(2); body.setRotZ(.3F); body.saveInitialSnapshot();
        body.setScaleX(0); body.setScaleZ(0); body.setPosX(90); body.setRotZ(-3);
        BirthPoseReset.restore(body);
        assertEquals(1.5F, body.getScaleX()); assertEquals(.75F, body.getScaleZ());
        assertEquals(2, body.getPosX()); assertEquals(.3F, body.getRotZ());
        BirthPoseReset.restore(body);
        assertEquals(1.5F, body.getScaleX());
    }
}
