package me.earth.pingbypass.server.handlers;

import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.*;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;

public interface IServerGamePacketListener extends ServerGamePacketListener {
    void onPacket(Packet<?> packet);

    @Override
    default void handleSignedChatCommand(ServerboundChatCommandSignedPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleCookieResponse(ServerboundCookieResponsePacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleDebugSampleSubscription(ServerboundDebugSampleSubscriptionPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleContainerSlotStateChanged(ServerboundContainerSlotStateChangedPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleConfigurationAcknowledged(ServerboundConfigurationAcknowledgedPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleChunkBatchReceived(ServerboundChunkBatchReceivedPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handlePingRequest(ServerboundPingRequestPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleAnimate(ServerboundSwingPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleChat(ServerboundChatPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleChatCommand(ServerboundChatCommandPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleChatAck(ServerboundChatAckPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleClientCommand(ServerboundClientCommandPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleClientInformation(ServerboundClientInformationPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleContainerButtonClick(ServerboundContainerButtonClickPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleContainerClick(ServerboundContainerClickPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handlePlaceRecipe(ServerboundPlaceRecipePacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleContainerClose(ServerboundContainerClosePacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleCustomPayload(ServerboundCustomPayloadPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleInteract(ServerboundInteractPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleKeepAlive(ServerboundKeepAlivePacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleMovePlayer(ServerboundMovePlayerPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handlePong(ServerboundPongPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handlePlayerAction(ServerboundPlayerActionPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handlePlayerCommand(ServerboundPlayerCommandPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handlePlayerInput(ServerboundPlayerInputPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleSetCarriedItem(ServerboundSetCarriedItemPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleSignUpdate(ServerboundSignUpdatePacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleUseItemOn(ServerboundUseItemOnPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleUseItem(ServerboundUseItemPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleResourcePackResponse(ServerboundResourcePackPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handlePaddleBoat(ServerboundPaddleBoatPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleMoveVehicle(ServerboundMoveVehiclePacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleRecipeBookSeenRecipePacket(ServerboundRecipeBookSeenRecipePacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleRecipeBookChangeSettingsPacket(ServerboundRecipeBookChangeSettingsPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleSeenAdvancements(ServerboundSeenAdvancementsPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleSetCommandBlock(ServerboundSetCommandBlockPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handlePickItem(ServerboundPickItemPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleRenameItem(ServerboundRenameItemPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleSetBeaconPacket(ServerboundSetBeaconPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleSetStructureBlock(ServerboundSetStructureBlockPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleSelectTrade(ServerboundSelectTradePacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleEditBook(ServerboundEditBookPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleEntityTagQuery(ServerboundEntityTagQueryPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleBlockEntityTagQuery(ServerboundBlockEntityTagQueryPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleJigsawGenerate(ServerboundJigsawGeneratePacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleChangeDifficulty(ServerboundChangeDifficultyPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleLockDifficulty(ServerboundLockDifficultyPacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void handleChatSessionUpdate(ServerboundChatSessionUpdatePacket packet) {
        this.onPacket(packet);
    }

    @Override
    default void onDisconnect(DisconnectionDetails details) {
    }
}
