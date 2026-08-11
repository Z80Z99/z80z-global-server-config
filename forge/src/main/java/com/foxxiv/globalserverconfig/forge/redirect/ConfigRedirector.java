package com.foxxiv.globalserverconfig.forge.redirect;

import com.foxxiv.globalserverconfig.config.ConfigManager;
import com.foxxiv.globalserverconfig.config.GscConfig;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

/**
 * Server config 路径重定向决策器（纯函数，无副作用）
 * 原理：根据 ModConfig.Type 和 modId 决定使用全局路径还是世界路径
 * 设计：独立出来便于测试，降低 Mixin 类耦合度（LoD 迪米特法则）
 */
public final class ConfigRedirector {

    private ConfigRedirector() {}

    /** 是否应该执行重定向逻辑（type==SERVER 且总开关开启） */
    public static boolean shouldRedirect(ModConfig.Type type) {
        if (type != ModConfig.Type.SERVER) {
            return false;
        }
        return ConfigManager.getConfig().enabled;
    }

    /**
     * 计算单个 config 应使用的路径
     * 原理：排除列表中的 mod 保持世界路径，其他使用全局路径
     *
     * @param config 当前要加载/卸载的 ModConfig
     * @param originalPath 原始 configBasePath（世界目录）
     * @return 实际应使用的路径
     */
    public static Path resolvePath(ModConfig config, Path originalPath) {
        GscConfig cfg = ConfigManager.getConfig();
        if (cfg.excludedModIds != null && cfg.excludedModIds.contains(config.getModId())) {
            return originalPath;
        }
        return ConfigManager.getGlobalServerConfigDir();
    }
}
