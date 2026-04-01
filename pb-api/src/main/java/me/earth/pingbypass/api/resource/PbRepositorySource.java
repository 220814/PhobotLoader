package me.earth.pingbypass.api.resource;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import me.earth.pingbypass.api.plugin.PluginConfig;
import me.earth.pingbypass.api.plugin.PluginConfigContainer;
import me.earth.pingbypass.api.Constants;
import me.earth.pingbypass.api.side.Side;
import me.earth.pingbypass.api.launch.PreLaunchService;
import me.earth.pingbypass.api.util.StreamUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackSelectionConfig; // Import mới
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@MethodsReturnNonnullByDefault
@ExtensionMethod(StreamUtil.class)
final class PbRepositorySource implements RepositorySource {
    private final PreLaunchService preLaunchService;

    @Override
    public void loadPacks(@NotNull Consumer<Pack> consumer) {
        Stream.concat(
                preLaunchService.getPluginDiscoveryService(Side.CLIENT).getPluginConfigs(),
                Stream.of(getPingBypassContainer())
        ).forEach(container -> {
            String namespace = container.getConfig().getName().toLowerCase();
            
            PackLocationInfo locationInfo = new PackLocationInfo(
                    namespace,
                    Constants.NAME_LOW.equals(namespace)
                            ? Component.literal("PingBypass resource pack")
                            : Component.literal("Plugin resource pack"),
                    PackSource.BUILT_IN,
                    Optional.empty()
            );

            /*
             * SỬA LỖI: 
             * 1. Pack.Selection -> PackSelectionConfig
             * 2. Pack.readMetaAndCreate yêu cầu: (info, supplier, type, selectionConfig)
             */
            PackSelectionConfig selectionConfig = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);

            Pack pack = Pack.readMetaAndCreate(
                    locationInfo,
                    new Pack.ResourcesSupplier() {
                        @Nullable
                        @Override
                        public PackResources openPrimary(PackLocationInfo info) {
                            return new PbPackResources(namespace, container.getPath());
                        }

                        @Nullable
                        @Override
                        public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                            return openPrimary(info);
                        }
                    },
                    PackType.CLIENT_RESOURCES,
                    selectionConfig
            );

            if (pack != null) {
                consumer.accept(pack);
            }
        });
    }

    private PluginConfigContainer getPingBypassContainer() {
        PluginConfig config = new PluginConfig();
        config.setName(Constants.NAME);
        return new PluginConfigContainer(config, null);
    }
}
