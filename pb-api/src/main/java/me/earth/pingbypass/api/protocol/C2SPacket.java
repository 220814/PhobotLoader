package me.earth.pingbypass.api.protocol;

import me.earth.pingbypass.api.util.packet.CustomPacket;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload; 

public interface C2SPacket extends CustomPacket, PBPacket {
    
    @Override
    default Integer getId(ConnectionProtocol protocol, PacketFlow flow) {
         return 0; 
    }

    @Override
    default ConnectionProtocol getProtocol() {
        return ConnectionProtocol.PLAY;
    }

    @Override
    default PacketFlow getFlow() {
        return PacketFlow.SERVERBOUND;
    }
}
