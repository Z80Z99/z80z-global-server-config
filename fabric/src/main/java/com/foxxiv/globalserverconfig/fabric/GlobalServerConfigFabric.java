package com.foxxiv.globalserverconfig.fabric;

import com.foxxiv.globalserverconfig.GlobalServerConfig;
import com.foxxiv.globalserverconfig.platform.PlatformService;
import net.fabricmc.api.ModInitializer;

/**
 * Fabric 模组入口
 * 原理：Fabric 端无 ConfigTracker 类，server config 重定向功能不生效
 *      仅加载配置文件并打印提示日志，保持 Architectury 双平台可加载
 */
public class GlobalServerConfigFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        PlatformService.setHelper(new FabricPlatformHelper());
        GlobalServerConfig.init();
        GlobalServerConfig.LOGGER.warn(
                "GlobalServerConfig: server config redirection is Forge-only. "
                        + "On Fabric this mod has no effect.");
    }
}
