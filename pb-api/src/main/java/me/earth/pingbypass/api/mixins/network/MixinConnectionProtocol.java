package me.earth.pingbypass.api.mixins.network;

import me.earth.pingbypass.api.ducks.network.IConnectionProtocol;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ConnectionProtocol.class)
public abstract class MixinConnectionProtocol implements IConnectionProtocol {

    @Override
    public int pingbypass_getId(PacketFlow packetFlow, Class<? extends Packet<?>> packet) {
        return -1; 
    }

}
