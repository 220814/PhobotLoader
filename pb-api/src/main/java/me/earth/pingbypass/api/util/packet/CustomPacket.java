package me.earth.pingbypass.api.util.packet;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;

public interface CustomPacket {

    Integer getId(ConnectionProtocol protocol, PacketFlow flow);

    ConnectionProtocol getProtocol();

    PacketFlow getFlow();

    default boolean isValidPacket(ConnectionProtocol protocol, PacketFlow flow) {
        return protocol.equals(getProtocol()) && flow.equals(getFlow());
    }
}
