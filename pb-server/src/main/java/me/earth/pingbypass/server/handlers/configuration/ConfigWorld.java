package me.earth.pingbypass.server.handlers.configuration;

import com.google.common.collect.Maps;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import lombok.RequiredArgsConstructor;
import me.earth.pingbypass.api.command.impl.arguments.NameableArgumentType;
import me.earth.pingbypass.api.traits.Nameable;
import me.earth.pingbypass.server.PingBypassServer;
import me.earth.pingbypass.server.commands.api.ServerCommandSource;
import me.earth.pingbypass.server.mixins.network.IClientboundPlayerInfoUpdatePacket;
import me.earth.pingbypass.server.session.Session;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.*;

@RequiredArgsConstructor
public class ConfigWorld {
    private final PingBypassServer server;
    private final Session session;

    public void placeNewPlayer(CommonListenerCookie cookie) {
        ResourceKey<Level> levelResourceKey = Level.END;

        Holder<DimensionType> dimensionTypeHolder = server.getMinecraft().getConnection().registryAccess()
                .lookupOrThrow(Registries.DIMENSION_TYPE)
                .getOrThrow(BuiltinDimensionTypes.END);

        CommonPlayerSpawnInfo spawnInfo = new CommonPlayerSpawnInfo(
                dimensionTypeHolder,
                levelResourceKey,
                BiomeManager.obfuscateSeed(0L),
                GameType.SPECTATOR,
                GameType.SPECTATOR,
                false,
                false,
                Optional.empty(),
                300
        );

        session.send(new ClientboundLoginPacket(0, false, Set.of(levelResourceKey), 1, 2, 2, false, false, false, spawnInfo, false));
        session.send(new ClientboundChangeDifficultyPacket(Difficulty.PEACEFUL, true));
        Abilities abilities = new Abilities();
        abilities.flying = true;
        session.send(new ClientboundPlayerAbilitiesPacket(abilities));
        session.send(new ClientboundSetCarriedItemPacket(0));
        session.send(new ClientboundUpdateRecipesPacket(Collections.emptyList()));
        Component header = Component.literal("Welcome to PingBypass!");
        Component footer = Component.literal("Use commands to configure the server.");
        session.send(new ClientboundTabListPacket(header, footer));
        sendCommands();
        session.send(new ClientboundPlayerPositionPacket(0.0, 240.0, 0.0, 0.0f, 0.0f, Collections.emptySet(), 0));
        var packet = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(Collections.emptyList());
        var entry = new ClientboundPlayerInfoUpdatePacket.Entry(cookie.gameProfile().getId(), cookie.gameProfile(), false, 0, GameType.SPECTATOR, null, null);
        ((IClientboundPlayerInfoUpdatePacket) packet).setEntries(List.of(entry));
        session.send(packet);
        session.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.LEVEL_CHUNKS_LOAD_START, 0.0f));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void sendCommands() {
        RootCommandNode<ServerCommandSource> root = server.getServerCommandManager().getRoot();
        Map<CommandNode<ServerCommandSource>, CommandNode<ServerCommandSource>> map = Maps.newHashMap();
        RootCommandNode<ServerCommandSource> rootCommandNode = new RootCommandNode<>();
        map.put(root, rootCommandNode);
        ServerCommandSource commandSource = new ServerCommandSource(server, session);
        this.fillUsableCommands(root, rootCommandNode, commandSource, map);
        session.send(new ClientboundCommandsPacket((RootCommandNode<SharedSuggestionProvider>) (RootCommandNode) rootCommandNode));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void fillUsableCommands(
            CommandNode<ServerCommandSource> rootCommandSource,
            CommandNode<ServerCommandSource> rootSuggestion,
            ServerCommandSource source,
            Map<CommandNode<ServerCommandSource>, CommandNode<ServerCommandSource>> commandNodeToSuggestionNode
    ) {
        var actualChildren = new ArrayList<CommandNode<ServerCommandSource>>();
        for (CommandNode<ServerCommandSource> node : rootCommandSource.getChildren()) {
            if (!node.canUse(source)) {
                continue;
            }

            if (node instanceof ArgumentCommandNode<?,?> arg && arg.getType() instanceof NameableArgumentType nameableArgumentType) {
                nameableArgumentType.getNameables().forEach(nameable -> {
                    var literal = new LiteralCommandNode<>(
                            ((Nameable) nameable).getName(),
                            node.getCommand(),
                            node.getRequirement(),
                            node.getRedirect(),
                            node.getRedirectModifier(),
                            node.isFork()
                    );

                    node.getChildren().forEach(literal::addChild);
                    actualChildren.add(literal);
                });
            } else {
                actualChildren.add(node);
            }
        }

        for (CommandNode<ServerCommandSource> commandNode : actualChildren) {
            if (commandNode.canUse(source)) {
                ArgumentBuilder<ServerCommandSource, ?> argumentBuilder = commandNode.createBuilder();
                argumentBuilder.requires(arg -> true);
                if (argumentBuilder.getCommand() != null) {
                    argumentBuilder.executes(commandContext -> 0);
                }

                if (argumentBuilder instanceof RequiredArgumentBuilder requiredArgumentBuilder && requiredArgumentBuilder.getSuggestionsProvider() != null) {
                    requiredArgumentBuilder.suggests(SuggestionProviders.safelySwap(requiredArgumentBuilder.getSuggestionsProvider()));
                }

                if (argumentBuilder.getRedirect() != null) {
                    argumentBuilder.redirect(commandNodeToSuggestionNode.get(argumentBuilder.getRedirect()));
                }

                CommandNode<ServerCommandSource> commandNode4 = argumentBuilder.build();
                commandNodeToSuggestionNode.put(commandNode, commandNode4);
                rootSuggestion.addChild(commandNode4);
                if (!commandNode.getChildren().isEmpty()) {
                    this.fillUsableCommands(commandNode, commandNode4, source, commandNodeToSuggestionNode);
                }
            }
        }
    }
    
}
            
