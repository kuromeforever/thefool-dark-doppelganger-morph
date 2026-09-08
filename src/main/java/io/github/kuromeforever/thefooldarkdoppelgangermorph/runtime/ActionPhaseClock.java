package io.github.kuromeforever.thefooldarkdoppelgangermorph.runtime;

import java.util.UUID;

/** The timing/order contract shared by packet admission and the controller clock. */
public final class ActionPhaseClock {
    private ActionPhaseClock() {}
    public static boolean acceptsStart(long sequence, long previousSequence, long startedAt,
            long now, int duration) {
        return sequence > previousSequence && duration > 0 && duration <= 6000
                && elapsed(startedAt, now) < duration;
    }
    public static boolean acceptsStop(UUID active, UUID stopped) {
        return active != null && active.equals(stopped);
    }
    public static boolean phaseActive(Object expectedDimension, Object actualDimension,
            long startedAt, long now, int duration) {
        return expectedDimension != null && expectedDimension.equals(actualDimension)
                && duration > 0 && duration <= 6000 && elapsed(startedAt, now) < duration;
    }
    /** The same Java entity can receive a different server UUID when BetterMorph reads its NBT. */
    public static boolean carrierMatches(Object expectedCarrier, Object actualCarrier,
            UUID expectedServerId, UUID actualServerId) {
        return expectedCarrier != null && expectedCarrier == actualCarrier
                && expectedServerId != null && expectedServerId.equals(actualServerId);
    }
    /** setIdentity runs before readNbt, so an unbound START cannot authenticate a constructor UUID. */
    public static boolean releaseBeforeIdentityNbt(Object boundCarrier, Object nextCarrier) {
        return boundCarrier != null && boundCarrier != nextCarrier;
    }
    public static long elapsed(long startedAt, long now) { return Math.max(0, now - startedAt); }
    public static double animationTick(long startedAt, long now, double partialTick, double speed) {
        return (elapsed(startedAt, now) + partialTick - Math.floor(partialTick)) * speed;
    }
    public static boolean owns(Object expectedCarrier, Object actualCarrier, Object expectedData,
            Object actualData, Object expectedSpell, Object actualSpell, int duration, int actualDuration,
            int previousRemaining, int remaining, boolean casting, boolean correctSource) {
        return expectedCarrier != null && expectedCarrier == actualCarrier && expectedData == actualData
                && expectedSpell == actualSpell && duration == actualDuration && remaining <= previousRemaining
                && casting && correctSource;
    }
}
