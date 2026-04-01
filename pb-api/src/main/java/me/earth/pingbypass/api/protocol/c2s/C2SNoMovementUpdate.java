package me.earth.pingbypass.api.protocol.c2s;

import me.earth.pingbypass.api.Constants;
import me.earth.pingbypass.api.protocol.C2SPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class C2SNoMovementUpdate implements C2SPacket {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Constants.NAME_LOW, "no-movement-update");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    /**
     * GIẢI PHÁP CUỐI CÙNG: 
     * Nếu compiler đòi type(), hãy cho nó type().
     * Nếu compiler đòi packetType(), hãy cho nó packetType().
     * Bạn có thể viết cả hai phương thức này mà không sợ lỗi, 
     * vì chúng chỉ là alias (tên gọi khác) dẫn về cùng một nguồn dữ liệu.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public PacketType<? extends Packet<PacketListener>> type() {
        return (PacketType<? extends Packet<PacketListener>>) (Object) Payload.TYPE;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public PacketType<? extends Packet<PacketListener>> packetType() {
        return (PacketType<? extends Packet<PacketListener>>) (Object) Payload.TYPE;
    }

    @Override
    public void writePacket(FriendlyByteBuf buf) {
        // No data
    }
}
