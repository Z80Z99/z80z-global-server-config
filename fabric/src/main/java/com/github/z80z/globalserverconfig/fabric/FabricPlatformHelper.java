package com.github.z80z.globalserverconfig.fabric;

import com.github.z80z.globalserverconfig.platform.IPlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

/** Fabric 平台实现：Fabric 本身无 serverconfig 概念，配置天然全局化 */
public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isClientSide() {
        // EnvType 是简单枚举，无 isClient() 方法，需直接与 CLIENT 比较
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public String getModVersion(String modId) {
        return FabricLoader.getInstance().getModContainer(modId)
                .map(c -> c.getMetadata().getVersion().getFriendlyString())
                .orElse("");
    }

    @Override
    public boolean supportsServerConfigRedirect() {
        // Fabric 无 ConfigTracker，本模组功能仅 Forge 可用
        return false;
    }
}
