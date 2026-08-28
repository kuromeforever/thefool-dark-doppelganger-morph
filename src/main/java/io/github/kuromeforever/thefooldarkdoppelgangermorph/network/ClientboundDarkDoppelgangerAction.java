package io.github.kuromeforever.thefooldarkdoppelgangermorph.network;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.client.DarkDoppelgangerClientActions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record ClientboundDarkDoppelgangerAction(
        UUID playerId,
        ResourceLocation actionId,
        boolean active,
        int durationTicks,
        UUID castId
) {
    static void encode(ClientboundDarkDoppelgangerAction message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.playerId);
        buffer.writeResourceLocation(message.actionId);
        buffer.writeBoolean(message.active);
        buffer.writeVarInt(message.durationTicks);
        buffer.writeUUID(message.castId);
    }

    static ClientboundDarkDoppelgangerAction decode(FriendlyByteBuf buffer) {
        return new ClientboundDarkDoppelgangerAction(
                buffer.readUUID(),
                buffer.readResourceLocation(),
                buffer.readBoolean(),
                buffer.readVarInt(),
                buffer.readUUID()
        );
    }

    static void handle(
            ClientboundDarkDoppelgangerAction message,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> DarkDoppelgangerClientActions.handle(message)
        ));
        context.setPacketHandled(true);
    }
}
