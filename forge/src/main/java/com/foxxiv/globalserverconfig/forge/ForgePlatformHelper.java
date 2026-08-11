package com.foxxiv.globalserverconfig.forge;

import com.foxxiv.globalserverconfig.platform.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

/** Forge 平台实现：通过 FMLPaths 获取配置目录 */
public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isClientSide() {
        return net.minecraftforge.api.distmarker.Dist.CLIENT.isClient();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public String getModVersion(String modId) {
        return ModList.get().getModContainerById(modId)
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("");
    }

    @Override
    public boolean supportsServerConfigRedirect() {
        return true;
    }
}
