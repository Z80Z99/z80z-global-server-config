package com.github.z80z.globalserverconfig.config;

import com.github.z80z.globalserverconfig.GlobalServerConfig;
import com.github.z80z.globalserverconfig.platform.PlatformService;
import com.github.z80z.globalserverconfig.util.GscConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 配置管理器
 * 原理：Gson 读写 JSON，volatile 保证可见性
 * 异常处理：单个 IO 异常不影响默认配置创建，避免阻塞游戏启动
 */
public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static volatile GscConfig config;

    private ConfigManager() {}

    public static GscConfig getConfig() {
        if (config == null) {
            synchronized (ConfigManager.class) {
                if (config == null) {
                    config = new GscConfig();
                }
            }
        }
        return config;
    }

    /** 加载配置；文件不存在则创建默认配置 */
    public static void loadConfig() {
        Path configPath = getConfigFilePath();
        try {
            if (Files.exists(configPath)) {
                String json = Files.readString(configPath);
                GscConfig loaded = GSON.fromJson(json, GscConfig.class);
                if (loaded == null) {
                    loaded = new GscConfig();
                }
                normalizeExcludedList(loaded);
                config = loaded;
                GlobalServerConfig.LOGGER.info("Config loaded from {}", configPath);
            } else {
                config = new GscConfig();
                saveConfig();
                GlobalServerConfig.LOGGER.info("Default config created at {}", configPath);
            }
        } catch (IOException | JsonSyntaxException e) {
            GlobalServerConfig.LOGGER.error("Failed to load config, using defaults", e);
            config = new GscConfig();
            saveConfig();
        }
    }

    /** 保存当前配置到文件 */
    public static void saveConfig() {
        Path configPath = getConfigFilePath();
        try {
            Files.createDirectories(configPath.getParent());
            String json = GSON.toJson(getConfig());
            Files.writeString(configPath, json,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            GlobalServerConfig.LOGGER.error("Failed to save config", e);
        }
    }

    /** 模组自身配置文件路径：config/z80z_global_server_config.json */
    private static Path getConfigFilePath() {
        return PlatformService.get().getConfigDir().resolve(GscConstants.SELF_CONFIG_FILE);
    }

    /** 全局 server config 目录：config/<targetSubDir>/ */
    public static Path getGlobalServerConfigDir() {
        return PlatformService.get().getConfigDir().resolve(getConfig().targetSubDir);
    }

    /** Gson 反序列化后转 CopyOnWriteArrayList 保证线程安全 */
    private static void normalizeExcludedList(GscConfig cfg) {
        if (cfg.excludedModIds == null) {
            cfg.excludedModIds = new CopyOnWriteArrayList<>();
        } else if (!(cfg.excludedModIds instanceof CopyOnWriteArrayList)) {
            cfg.excludedModIds = new CopyOnWriteArrayList<>(cfg.excludedModIds);
        }
    }
}
