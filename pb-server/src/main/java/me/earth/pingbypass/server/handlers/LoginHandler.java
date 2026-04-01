package me.earth.pingbypass.server.handlers;

import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.earth.pingbypass.server.PingBypassServer;
import me.earth.pingbypass.server.ServerConstants;
import me.earth.pingbypass.server.session.Session;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.login.*;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import org.apache.commons.lang3.Validate;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class LoginHandler implements ServerLoginPacketListener, TickablePacketListener, IHandler {
    private static final Component DISCONNECT_UNEXPECTED_QUERY = Component.translatable("multiplayer.disconnect.unexpected_query_response");
    private static final int MAX_TICKS_BEFORE_LOGIN = 600;

    private final byte[] challenge = Ints.toByteArray(RandomSource.create().nextInt());
    private final PingBypassServer server;
    @Getter
    private final Session session;

    private volatile State state = State.HELLO;
    private int tick;
    private String requestedUsername;
    private GameProfile authenticatedProfile;

    @Override
    public void tick() {
        if (state == State.VERIFYING) {
            verifyLoginAndFinishConnectionSetup(Objects.requireNonNull(authenticatedProfile));
        }
        if (state == State.WAITING_FOR_DUPE_DISCONNECT) {
            finishLoginAndWaitForClient(Objects.requireNonNull(authenticatedProfile));
        }
        if (tick++ == MAX_TICKS_BEFORE_LOGIN) {
            disconnect(Component.translatable("multiplayer.disconnect.slow_login"));
        }
    }

    @Override
    public void onDisconnect(DisconnectionDetails details) {
        log.info("{} lost connection: {}", getUserName(), details.reason().getString());
    }

    @Override
    public void handleHello(ServerboundHelloPacket packet) {
        Validate.validState(state == State.HELLO, "Unexpected hello packet");
        Validate.validState(StringUtil.isValidPlayerName(packet.name()), "Invalid characters in username");
        requestedUsername = packet.name();
        if (server.getServerConfig().get(ServerConstants.AUTH) && !session.isMemoryConnection()) {
            state = State.KEY;
            session.send(new ClientboundHelloPacket("", ServerConstants.KEY_PAIR.getPublic().getEncoded(), challenge, true));
        } else {
            startClientVerification(UUIDUtil.createOfflineProfile(requestedUsername));
        }
    }

    @Override
    public void handleKey(ServerboundKeyPacket packet) {
        Validate.validState(state == State.KEY, "Unexpected key packet");
        try {
            PrivateKey privateKey = ServerConstants.KEY_PAIR.getPrivate();
            if (!packet.isChallengeValid(challenge, privateKey)) {
                throw new IllegalStateException("Protocol error");
            }
            SecretKey secretKey = packet.getSecretKey(privateKey);
            Cipher cipher = Crypt.getCipher(2, secretKey);
            Cipher cipher2 = Crypt.getCipher(1, secretKey);
            state = State.AUTHENTICATING;
            session.setEncryptionKey(cipher, cipher2);
        } catch (CryptException e) {
            throw new IllegalStateException("Protocol error", e);
        }
        startClientVerification(UUIDUtil.createOfflineProfile(Objects.requireNonNull(requestedUsername)));
    }

    @Override
    public void handleCustomQueryPacket(ServerboundCustomQueryAnswerPacket packet) {
        disconnect(DISCONNECT_UNEXPECTED_QUERY);
    }

    @Override
    public void handleLoginAcknowledgement(ServerboundLoginAcknowledgedPacket packet) {
        Validate.validState(state == State.PROTOCOL_SWITCHING, "Unexpected login acknowledgement packet");
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(Objects.requireNonNull(authenticatedProfile), false);
        session.setCookie(cookie);
        ConfigurationPacketListener configurationPacketListener = new ConfigurationPacketListener(server, session, cookie);
        session.setupConfigurationListener(configurationPacketListener);
        configurationPacketListener.startConfiguration();
        state = State.ACCEPTED;
    }

    @Override
    public void handleCookieResponse(ServerboundCookieResponsePacket packet) {
    }

    public void fillListenerSpecificCrashDetails(CrashReportCategory crashReportCategory) {
        crashReportCategory.setDetail("Login phase", () -> state.toString());
    }

    @Override
    public boolean isAcceptingMessages() {
        return session.isOpen();
    }

    private void disconnect(Component reason) {
        try {
            server.getSessionManager().disconnect(session, reason);
        } catch (Exception e) {
            log.error("Error whilst disconnecting player", e);
        }
    }

    private String getUserName() {
        String string = session.getLoggableAddress(server.getServerConfig().get(ServerConstants.LOG_IPS));
        return requestedUsername != null ? requestedUsername + " (" + string + ")" : string;
    }

    private void startClientVerification(GameProfile authenticatedProfile) {
        this.authenticatedProfile = authenticatedProfile;
        state = State.VERIFYING;
    }

    private void verifyLoginAndFinishConnectionSetup(GameProfile profile) {
        int compression = server.getServerConfig().get(ServerConstants.COMPRESSION);
        if (compression >= 0 && !session.isMemoryConnection()) {
            session.send(new ClientboundLoginCompressionPacket(compression),
                    PacketSendListener.thenRun(() -> session.setupCompression(compression, true)));
        }
        state = State.WAITING_FOR_DUPE_DISCONNECT;
        finishLoginAndWaitForClient(profile);
    }

    private void finishLoginAndWaitForClient(GameProfile profile) {
        state = State.PROTOCOL_SWITCHING;
        session.send(new ClientboundGameProfilePacket(profile, false));
    }

    private enum State {
        HELLO, KEY, AUTHENTICATING, VERIFYING, WAITING_FOR_DUPE_DISCONNECT, PROTOCOL_SWITCHING, ACCEPTED
    }
}
