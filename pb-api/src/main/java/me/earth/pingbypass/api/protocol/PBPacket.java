package me.earth.pingbypass.api.protocol;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface PBPacket extends Packet<PacketListener> {
    ResourceLocation getId();

    void writePacket(FriendlyByteBuf buf);

    @Override
    default void handle(PacketListener handler) {
    }

    @NotNull
    @SuppressWarnings("unchecked")
    default PacketType<? extends Packet<PacketListener>> packetType() {
        return (PacketType<? extends Packet<PacketListener>>) (Object) Payload.TYPE;
    }

    record Payload(PBPacket packet) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<Payload> TYPE =
                new CustomPacketPayload.Type<>(ResourceLocation.parse("pb:packet"));

        public void write(RegistryFriendlyByteBuf buffer) {
            packet.writePacket(buffer);
        }

        @Override
        @NotNull
        public CustomPacketPayload.Type<Payload> type() {
            return TYPE;
        }
    }
}
