package me.earth.pingbypass.server.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.earth.pingbypass.server.PingBypassServer;
import me.earth.pingbypass.server.session.Session;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatusPacketListener;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class StatusHandler implements ServerStatusPacketListener, IHandler {
    private static final Component DISCONNECT_REASON = Component.translatable("multiplayer.status.request_handled");
    private final PingBypassServer server;
    @Getter
    private final Session session;
    private boolean hasRequestedStatus;

    @Override
    public void onDisconnect(DisconnectionDetails details) {
    }

    @Override
    public void handleStatusRequest(@NotNull ServerboundStatusRequestPacket packet) {
        if (hasRequestedStatus) {
            server.getSessionManager().disconnect(session, DISCONNECT_REASON);
        } else {
            hasRequestedStatus = true;
            session.send(new ClientboundStatusResponsePacket(server.getServerStatusService().getServerStatus()));
        }
    }

    @Override
    public void handlePingRequest(ServerboundPingRequestPacket packet) {
        session.send(new ClientboundPongResponsePacket(packet.getTime()));
        server.getSessionManager().disconnect(session, DISCONNECT_REASON);
    }
}
