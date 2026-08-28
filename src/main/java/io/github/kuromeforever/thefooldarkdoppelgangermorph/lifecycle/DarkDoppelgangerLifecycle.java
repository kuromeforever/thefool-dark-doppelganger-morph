package io.github.kuromeforever.thefooldarkdoppelgangermorph.lifecycle;

import com.kurome.ageofmythology.bettermorph.impl.common.MorphSpellCastCoordinator;
import dev.architectury.event.EventResult;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.client.DarkDoppelgangerClientActions;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.network.DarkDoppelgangerActionSessions;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.runtime.BladeComboService;
import liushuangwuyin.bettermorph.api.event.IdentityChangedCallback;
import liushuangwuyin.bettermorph.api.event.IdentitySwapCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;

public final class DarkDoppelgangerLifecycle {
    private static final DarkDoppelgangerLifecycle INSTANCE = new DarkDoppelgangerLifecycle();
    private static boolean initialized;

    private DarkDoppelgangerLifecycle() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        IdentitySwapCallback.EVENT.register((player, newIdentity) -> {
            clear(player);
            return EventResult.pass();
        });
        IdentityChangedCallback.EVENT.register((player, newIdentity) -> {
            clear(player);
            return EventResult.pass();
        });
        initialized = true;
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            DarkDoppelgangerActionSessions.tick(event.getServer());
        }
    }

    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        DarkDoppelgangerActionSessions.beginServerSession();
        BladeComboService.clearAll();
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            clear(player);
        }
    }

    @SubscribeEvent
    public void onClone(PlayerEvent.Clone event) {
        if (event.getOriginal() instanceof ServerPlayer original) {
            clear(original);
        }
        if (event.getEntity() instanceof ServerPlayer clone) {
            clear(clone);
        }
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            clear(player);
        }
    }

    @SubscribeEvent
    public void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            clear(player);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer recipient
                && event.getTarget() instanceof ServerPlayer trackedPlayer) {
            DarkDoppelgangerActionSessions.syncToTracking(recipient, trackedPlayer);
        }
    }

    @SubscribeEvent
    public void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            DarkDoppelgangerActionSessions.clearLevel(level.dimension());
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        DarkDoppelgangerActionSessions.clearAll();
        BladeComboService.clearAll();
    }

    private static void clear(net.minecraft.world.entity.player.Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            BladeComboService.clear(serverPlayer.getUUID());
            DarkDoppelgangerActionSessions.clearPlayer(serverPlayer.getServer(), serverPlayer.getUUID());
            MorphSpellCastCoordinator.cancel(serverPlayer);
        } else if (player != null && player.level().isClientSide) {
            DistExecutor.unsafeRunWhenOn(
                    Dist.CLIENT,
                    () -> () -> DarkDoppelgangerClientActions.clearPlayer(player.getUUID())
            );
        }
    }
}
