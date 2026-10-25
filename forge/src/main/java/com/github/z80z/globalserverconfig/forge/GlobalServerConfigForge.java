package com.github.z80z.globalserverconfig.forge;

import com.github.z80z.globalserverconfig.GlobalServerConfig;
import com.github.z80z.globalserverconfig.gui.ModConfigScreen;
import com.github.z80z.globalserverconfig.platform.PlatformService;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge 模组入口
 * 原理：@Mod 注解触发构造函数，注入平台实现并初始化主类
 * 注册 ConfigScreenFactory 提供 Mod 列表中的配置界面入口
 */
@Mod(GlobalServerConfig.MOD_ID)
public class GlobalServerConfigForge {

    public GlobalServerConfigForge() {
        PlatformService.setHelper(new ForgePlatformHelper());
        GlobalServerConfig.init();
        registerConfigScreen();
    }

    private void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, screen) -> ModConfigScreen.create(screen)
                )
        );
    }
}
