package com.foxxiv.globalserverconfig;

import com.foxxiv.globalserverconfig.config.ConfigManager;
import com.foxxiv.globalserverconfig.util.GscConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模组主类
 * 原理：通过 init() 入口完成配置加载；具体重定向逻辑由各平台子模块实现
 * 线程安全：volatile + 双重检查锁定
 */
public final class GlobalServerConfig {
    public static final String MOD_ID = GscConstants.MOD_ID;
    public static final String MOD_NAME = GscConstants.MOD_NAME;
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    private static volatile boolean initialized = false;

    private GlobalServerConfig() {}

    public static void init() {
        if (initialized) return;
        synchronized (GlobalServerConfig.class) {
            if (initialized) return;
            initialized = true;
            LOGGER.info("Initializing GlobalServerConfig...");
            ConfigManager.loadConfig();
            LOGGER.info("GlobalServerConfig initialized.");
        }
    }
}
