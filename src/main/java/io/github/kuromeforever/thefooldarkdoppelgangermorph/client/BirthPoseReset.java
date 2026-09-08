package io.github.kuromeforever.thefooldarkdoppelgangermorph.client;

import software.bernie.geckolib.cache.object.GeoBone;

/** Restores the source rest pose without inventing scale or rotation values. */
public final class BirthPoseReset {
    private BirthPoseReset() {}
    public static void restore(GeoBone bone) {
        var rest = bone.getInitialSnapshot();
        if (rest == null) return;
        bone.setScaleX(rest.getScaleX()); bone.setScaleY(rest.getScaleY()); bone.setScaleZ(rest.getScaleZ());
        bone.setPosX(rest.getOffsetX()); bone.setPosY(rest.getOffsetY()); bone.setPosZ(rest.getOffsetZ());
        bone.setRotX(rest.getRotX()); bone.setRotY(rest.getRotY()); bone.setRotZ(rest.getRotZ());
    }
}
