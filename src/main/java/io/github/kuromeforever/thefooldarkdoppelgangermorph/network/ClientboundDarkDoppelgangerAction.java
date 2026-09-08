package io.github.kuromeforever.thefooldarkdoppelgangermorph.network;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.client.DarkDoppelgangerClientActions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import java.util.UUID;
import java.util.function.Supplier;

/** A phase snapshot. The start timestamp also restores progress for a late observer. */
public record ClientboundDarkDoppelgangerAction(
        UUID playerId, ResourceLocation actionId, boolean active, int durationTicks, UUID castId,
        UUID carrierId, ResourceLocation dimension, long startedAt, long sequence,
        boolean finishing, int castDuration
) {
    static void encode(ClientboundDarkDoppelgangerAction message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.playerId); buffer.writeResourceLocation(message.actionId);
        buffer.writeBoolean(message.active); buffer.writeVarInt(message.durationTicks);
        buffer.writeUUID(message.castId); buffer.writeUUID(message.carrierId);
        buffer.writeResourceLocation(message.dimension); buffer.writeLong(message.startedAt);
        buffer.writeLong(message.sequence); buffer.writeBoolean(message.finishing);
        buffer.writeVarInt(message.castDuration);
    }
    static ClientboundDarkDoppelgangerAction decode(FriendlyByteBuf buffer) {
        return new ClientboundDarkDoppelgangerAction(buffer.readUUID(), buffer.readResourceLocation(),
                buffer.readBoolean(), buffer.readVarInt(), buffer.readUUID(), buffer.readUUID(),
                buffer.readResourceLocation(), buffer.readLong(), buffer.readLong(), buffer.readBoolean(),
                buffer.readVarInt());
    }
    public ClientboundDarkDoppelgangerAction stopped() {
        return new ClientboundDarkDoppelgangerAction(playerId, actionId, false, 0, castId,
                carrierId, dimension, startedAt, sequence, finishing, castDuration);
    }
    public long elapsed(long now) { return Math.max(0, now - startedAt); }
    static void handle(ClientboundDarkDoppelgangerAction message, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> DarkDoppelgangerClientActions.handle(message)));
        context.setPacketHandled(true);
    }
}
