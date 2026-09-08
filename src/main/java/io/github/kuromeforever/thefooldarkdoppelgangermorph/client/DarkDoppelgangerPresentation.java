package io.github.kuromeforever.thefooldarkdoppelgangermorph.client;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.accessor.DarkDoppelgangerPresentationAccess;
import liushuangwuyin.bettermorph.api.PlayerIdentity;
import liushuangwuyin.bettermorph.api.model.EntityUpdater;
import liushuangwuyin.bettermorph.api.model.EntityUpdaters;
import net.bandit.darkdoppelganger.entity.DarkDoppelgangerEntity;
import net.bandit.darkdoppelganger.entity.EntityRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class DarkDoppelgangerPresentation {
    private static boolean registered;
    private static final java.util.Map<DarkDoppelgangerEntity, java.lang.ref.WeakReference<Player>> OWNERS = new java.util.WeakHashMap<>();
    private static final java.util.Set<DarkDoppelgangerEntity> PREVIEWS = java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>());
    private DarkDoppelgangerPresentation() {}

    public static void register() {
        if (registered) return;
        EntityUpdater<DarkDoppelgangerEntity> previous = EntityUpdaters.getUpdater(EntityRegistry.DARK_DOPPELGANGER.get());
        EntityUpdaters.register(EntityRegistry.DARK_DOPPELGANGER.get(), (player, identity) -> {
            if (PlayerIdentity.getIdentity(player) != identity) return;
            if (previous != null) previous.update(player, identity);
            prepareIdentity(player, identity);
            DarkDoppelgangerClientActions.applyPending(player, identity);
        });
        registered = true;
    }

    public static void prepareIdentity(Player player, DarkDoppelgangerEntity identity) {
        if (player != null && player.level().isClientSide && PlayerIdentity.getIdentity(player) == identity) {
            var owner = OWNERS.get(identity);
            if (owner == null || owner.get() != player) OWNERS.put(identity, new java.lang.ref.WeakReference<>(player));
            prepare(identity);
        }
    }

    public static void preparePreview(LivingEntity entity) {
        if (entity instanceof DarkDoppelgangerEntity identity && identity.level().isClientSide
                && identity.level().getEntity(identity.getId()) != identity) {
            PREVIEWS.add(identity);
            prepare(identity);
        }
    }
    public static boolean isProjection(DarkDoppelgangerEntity entity) {
        if (entity.level().getEntity(entity.getId()) == entity) return false;
        var reference = OWNERS.get(entity);
        Player owner = reference == null ? null : reference.get();
        return (owner != null && PlayerIdentity.getIdentity(owner) == entity) || PREVIEWS.contains(entity);
    }
    public static void clear() { OWNERS.clear(); PREVIEWS.clear(); }

    private static void prepare(DarkDoppelgangerEntity identity) {
        DarkDoppelgangerPresentationAccess state = (DarkDoppelgangerPresentationAccess) identity;
        if (state.doppel$getPresentationAge() > 45) return;
        state.doppel$setPresentationAge(46);
        state.doppel$getSpawnController().stop();
        state.doppel$getSpawnController().forceAnimationReset();
        var renderer = net.minecraft.client.Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(identity);
        if (renderer instanceof software.bernie.geckolib.renderer.GeoEntityRenderer<?> geoRenderer) {
            geoRenderer.getGeoModel().getBone("body").ifPresent(BirthPoseReset::restore);
        }
    }

}
