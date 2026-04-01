package me.earth.pingbypass.api.protocol;

import me.earth.pingbypass.api.util.packet.CustomPacket;
import net.minecraft.network.FriendlyByteBuf;

public interface S2CPacket extends CustomPacket, PBPacket {

    default void write(FriendlyByteBuf buffer) {
        // Ghi ID packet theo chuẩn 1.21.x
        buffer.writeResourceLocation(getId());

        // Ghi payload thực tế
        this.writePacket(buffer);
    }
}
