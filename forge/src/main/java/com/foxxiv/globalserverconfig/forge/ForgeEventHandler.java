package com.foxxiv.globalserverconfig.forge;

import com.foxxiv.globalserverconfig.GlobalServerConfig;
import com.foxxiv.globalserverconfig.config.ConfigManager;
import com.foxxiv.globalserverconfig.forge.migration.ServerConfigMigrator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

/**
 * Forge 服务端事件处理
 * 原理：监听 ServerAboutToStartEvent，在 ConfigTracker.loadConfigs 之前触发迁移
 * 时机：MinecraftServer.m_7038_ → ServerAboutToStartEvent → handleServerAboutToStart → loadConfigs
 *      迁移在 loadConfigs 之前完成，保证加载的是迁移后的全局文件
 */
@Mod.EventBusSubscriber(modid = GlobalServerConfig.MOD_ID)
public final class ForgeEventHandler {

    // MC 1.20.1 的 LevelResource 仅有 DATAPACK_DIR 静态字段，SERVERCONFIG 常量在 1.20.5+ 才加入
    // 这里直接构造，与 Forge ConfigTracker 内部获取 serverconfig 路径的方式一致
    private static final LevelResource SERVERCONFIG_RESOURCE = new LevelResource("serverconfig");

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        if (!ConfigManager.getConfig().enabled) {
            return;
        }
        if (!ConfigManager.getConfig().migrationEnabled) {
            return;
        }
        MinecraftServer server = event.getServer();
        Path worldServerConfig = server.getWorldPath(SERVERCONFIG_RESOURCE);
        Path globalServerConfig = ConfigManager.getGlobalServerConfigDir();
        GlobalServerConfig.LOGGER.info("Starting server config migration: {} -> {}",
                worldServerConfig, globalServerConfig);
        ServerConfigMigrator.migrate(worldServerConfig, globalServerConfig);
    }
}
