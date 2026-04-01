package me.earth.pingbypass.server.handlers.play;

import lombok.extern.slf4j.Slf4j;
import me.earth.pingbypass.server.PingBypassServer;
import me.earth.pingbypass.server.handlers.IServerGamePacketListener;
import me.earth.pingbypass.server.session.Session;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.game.ServerboundDebugSampleSubscriptionPacket;
import net.minecraft.server.network.CommonListenerCookie;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class PlayPacketHandler extends AbstractPlayPacketHandler implements IServerGamePacketListener {
    public PlayPacketHandler(PingBypassServer server, Session session, CommonListenerCookie cookie) {
        super(server, session, cookie);
    }

    @Override
    public @NotNull Connection getConnection() {
        return super.getConnection();
    }

    @Override
    public void onDisconnect(@NotNull DisconnectionDetails details) {
        log.info("Session disconnected {} {}", session, details.reason().getString());
    }

    @Override
    public void handleCookieResponse(ServerboundCookieResponsePacket packet) {}

    @Override
    public void handleDebugSampleSubscription(ServerboundDebugSampleSubscriptionPacket packet) {}
}
