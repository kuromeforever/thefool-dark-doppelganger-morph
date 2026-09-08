package io.github.kuromeforever.thefooldarkdoppelgangermorph.network;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.TheFoolDarkDoppelgangerMorph;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class DarkDoppelgangerNetwork {
    public static final String PROTOCOL = "2";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(TheFoolDarkDoppelgangerMorph.MOD_ID, "main"))
            .networkProtocolVersion(() -> PROTOCOL).clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals).simpleChannel();
    private static boolean initialized;
    private DarkDoppelgangerNetwork() {}
    public static synchronized void initialize() {
        if (initialized) return;
        CHANNEL.messageBuilder(ClientboundDarkDoppelgangerAction.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientboundDarkDoppelgangerAction::encode).decoder(ClientboundDarkDoppelgangerAction::decode)
                .consumerMainThread(ClientboundDarkDoppelgangerAction::handle).add();
        initialized = true;
    }
    public static void broadcast(ServerPlayer player, ClientboundDarkDoppelgangerAction message) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), message);
    }
    public static void sendTo(ServerPlayer recipient, ClientboundDarkDoppelgangerAction message) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> recipient), message);
    }
}
