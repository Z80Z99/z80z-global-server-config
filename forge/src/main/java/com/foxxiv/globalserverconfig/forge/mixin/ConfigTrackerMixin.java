package com.foxxiv.globalserverconfig.forge.mixin;

import com.foxxiv.globalserverconfig.GlobalServerConfig;
import com.foxxiv.globalserverconfig.forge.redirect.ConfigRedirector;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Set;

/**
 * 拦截 Forge ConfigTracker 的 loadConfigs / unloadConfigs
 *
 * 原理：在 HEAD 处注入，当 type==SERVER 且总开关开启时 cancel 原方法，
 *      自行遍历 configSets，对每个 config 根据 modId 决定使用全局路径还是世界路径
 *
 * 为何不用 @ModifyArg：loadConfigs 内部 forEach 共用一个 configBasePath，
 *      无法针对单个 mod 跳过；必须 cancel 后自己循环
 *
 * 异常处理：单个 mod 加载失败 fallback 到世界路径，再失败仅记日志不中断
 *
 * 资源管理：load 和 unload 必须用相同路径，否则文件 watcher 泄漏
 */
@Mixin(ConfigTracker.class)
public abstract class ConfigTrackerMixin {

    @Shadow(remap = false)
    private EnumMap<ModConfig.Type, Set<ModConfig>> configSets;

    @Shadow(remap = false)
    private void openConfig(ModConfig config, Path configBasePath) {
        // 由 Forge 实现
    }

    @Shadow(remap = false)
    private void closeConfig(ModConfig config, Path configBasePath) {
        // 由 Forge 实现
    }

    @Inject(method = "loadConfigs", at = @At("HEAD"), cancellable = true, remap = false)
    private void gsc$onLoadConfigs(ModConfig.Type type, Path configBasePath, CallbackInfo ci) {
        if (!ConfigRedirector.shouldRedirect(type)) {
            return;
        }
        ci.cancel();
        Set<ModConfig> configs = this.configSets.get(type);
        if (configs == null) {
            return;
        }
        for (ModConfig config : configs) {
            Path target = ConfigRedirector.resolvePath(config, configBasePath);
            try {
                this.openConfig(config, target);
            } catch (Exception e) {
                GlobalServerConfig.LOGGER.error(
                        "Failed to load server config for mod {} from {}, falling back to world path",
                        config.getModId(), target, e);
                try {
                    this.openConfig(config, configBasePath);
                } catch (Exception fallback) {
                    GlobalServerConfig.LOGGER.error(
                            "Fallback also failed for mod {}", config.getModId(), fallback);
                }
            }
        }
    }

    @Inject(method = "unloadConfigs", at = @At("HEAD"), cancellable = true, remap = false)
    private void gsc$onUnloadConfigs(ModConfig.Type type, Path configBasePath, CallbackInfo ci) {
        if (!ConfigRedirector.shouldRedirect(type)) {
            return;
        }
        ci.cancel();
        Set<ModConfig> configs = this.configSets.get(type);
        if (configs == null) {
            return;
        }
        for (ModConfig config : configs) {
            // 必须用与 loadConfigs 完全相同的路径决策，否则 watcher 卸载失败
            Path target = ConfigRedirector.resolvePath(config, configBasePath);
            try {
                this.closeConfig(config, target);
            } catch (Exception e) {
                GlobalServerConfig.LOGGER.error(
                        "Failed to unload server config for mod {}", config.getModId(), e);
            }
        }
    }
}
