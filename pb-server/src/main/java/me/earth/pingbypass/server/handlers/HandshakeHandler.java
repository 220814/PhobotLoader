package me.earth.pingbypass.server.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.earth.pingbypass.server.PingBypassServer;
import me.earth.pingbypass.server.ServerConstants;
import me.earth.pingbypass.server.session.Session;
import net.minecraft.SharedConstants;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class HandshakeHandler implements ServerHandshakePacketListener, IHandler {
    private static final Component IGNORE_STATUS_REASON = Component.literal("Ignoring status request");

    private final PingBypassServer server;
    @Getter
    private final Session session;

    @Override
    public void handleIntention(@NotNull ClientIntentionPacket packet) {
        switch (packet.intention()) {
            case LOGIN -> {
                if (packet.protocolVersion() != SharedConstants.getCurrentVersion().getProtocolVersion()) {
                    Component component;
                    if (packet.protocolVersion() < SharedConstants.getCurrentVersion().getProtocolVersion()) {
                        component = Component.translatable("multiplayer.disconnect.outdated_client", SharedConstants.getCurrentVersion().getName());
                    } else {
                        component = Component.translatable("multiplayer.disconnect.incompatible", SharedConstants.getCurrentVersion().getName());
                    }
                    server.getSessionManager().disconnect(session, component);
                } else {
                    session.setupLoginHandler(new LoginHandler(server, session));
                }
            }
            case STATUS -> {
                if (this.server.getServerConfig().get(ServerConstants.REPLY_TO_STATUS)) {
                    session.setupStatusHandler(new StatusHandler(server, session));
                } else {
                    server.getSessionManager().disconnect(session, IGNORE_STATUS_REASON);
                }
            }
            case TRANSFER -> {
                server.getSessionManager().disconnect(session, Component.literal("Transfer not supported"));
            }
            default -> throw new UnsupportedOperationException("Invalid intention " + packet.intention());
        }
    }

    @Override
    public void onDisconnect(DisconnectionDetails details) {
    }

    @Override
    public boolean isAcceptingMessages() {
        return session.isOpen();
    }
}
    
