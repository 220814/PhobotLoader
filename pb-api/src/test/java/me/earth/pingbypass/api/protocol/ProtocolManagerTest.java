package me.earth.pingbypass.api.protocol;

import io.netty.buffer.Unpooled;
import lombok.RequiredArgsConstructor;
import me.earth.pingbypass.PingBypass;
import me.earth.pingbypass.api.ducks.DummyConnection;
import me.earth.pingbypass.api.event.network.PacketEvent;
import me.earth.pingbypass.api.protocol.event.CustomPayloadInitEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProtocolManagerTest {
    private static final ThreadLocal<Boolean> HANDLED = ThreadLocal.withInitial(() -> false);

    @Test
    public void testProtocolManager() {
        HANDLED.set(false);
        ProtocolManager protocolManager = new ProtocolManager(null);

        TestPacket packet = new TestPacket(10);
        protocolManager.register(packet.getId(), TestPacket::new);

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(packet.getId());
        packet.write(buf);

        ProtocolListener protocolListener = new ProtocolListener(protocolManager, PacketFlow.SERVERBOUND);
        ResourceLocation resourceLocation = buf.readResourceLocation();
        
        CustomPayloadInitEvent payloadInitEvent = new CustomPayloadInitEvent(ServerboundCustomPayloadPacket.class, resourceLocation, buf);
        protocolListener.onCustomPayloadInit(payloadInitEvent);
        
        protocolListener.onPacketReceived(payloadInitEvent.getPayload(), new PacketEvent.Receive<>(null, new DummyConnection(PacketFlow.CLIENTBOUND)));
        assertFalse(HANDLED.get());
        
        protocolListener.onPacketReceived(payloadInitEvent.getPayload(), new PacketEvent.Receive<>(null, new DummyConnection(PacketFlow.SERVERBOUND)));
        assertTrue(HANDLED.get());
    }

    @RequiredArgsConstructor
    private static final class TestPacket implements C2SPacket, ProtocolHandler.SelfHandling {
        private final int value;
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("pingbypass", "test");

        public TestPacket(FriendlyByteBuf buf) {
            this(buf.readInt());
        }

        public void handle(PingBypass pingBypass, Connection connection) {
            HANDLED.set(true);
        }

        public @NotNull ResourceLocation getId() {
            return ID;
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeInt(value);
        }

        public void writePacket(FriendlyByteBuf buf) {
            write(buf);
        }

        public PacketType<TestPacket> type() {
            return new PacketType<>(PacketFlow.SERVERBOUND, ID);
        }

        public CustomPacketPayload asPayload() {
            return new CustomPacketPayload() {
                public @NotNull Type<? extends CustomPacketPayload> type() {
                    return new Type<>(ID);
                }

                public void write(FriendlyByteBuf buf) {
                    TestPacket.this.write(buf);
                }
            };
        }
    }
       
}
            
