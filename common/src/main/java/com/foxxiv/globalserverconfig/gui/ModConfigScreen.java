package com.foxxiv.globalserverconfig.gui;

import com.foxxiv.globalserverconfig.config.ConfigManager;
import com.foxxiv.globalserverconfig.config.GscConfig;
import com.foxxiv.globalserverconfig.util.GscConstants;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 模组配置界面（Cloth Config 实现）
 * 原理：通过 ConfigBuilder 构建配置 GUI，保存时调用 ConfigManager.saveConfig()
 * 平台：common 模块定义，两端共享
 */
public final class ModConfigScreen {
    private static final String LANG_PREFIX = "text.autoconfig." + GscConstants.MOD_ID;

    private ModConfigScreen() {}

    public static Screen create(Screen parent) {
        GscConfig cfg = ConfigManager.getConfig();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable(LANG_PREFIX + ".title"))
                .setSavingRunnable(ModConfigScreen::onSave);
        ConfigEntryBuilder eb = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(
                Component.translatable(LANG_PREFIX + ".category.general"));

        // 总开关
        general.addEntry(eb.startBooleanToggle(
                        Component.translatable(LANG_PREFIX + ".option.enabled"), cfg.enabled)
                .setDefaultValue(true)
                .setSaveConsumer(v -> cfg.enabled = v)
                .setTooltip(Component.translatable(LANG_PREFIX + ".option.enabled.tooltip"))
                .build());

        // 首次启动迁移开关
        general.addEntry(eb.startBooleanToggle(
                        Component.translatable(LANG_PREFIX + ".option.migrationEnabled"), cfg.migrationEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(v -> cfg.migrationEnabled = v)
                .setTooltip(Component.translatable(LANG_PREFIX + ".option.migrationEnabled.tooltip"))
                .build());

        // 全局子目录名
        general.addEntry(eb.startStrField(
                        Component.translatable(LANG_PREFIX + ".option.targetSubDir"), cfg.targetSubDir)
                .setDefaultValue("serverconfig")
                .setSaveConsumer(v -> cfg.targetSubDir = v)
                .setTooltip(Component.translatable(LANG_PREFIX + ".option.targetSubDir.tooltip"))
                .build());

        // 排除列表
        general.addEntry(eb.startStrList(
                        Component.translatable(LANG_PREFIX + ".option.excludedModIds"),
                        new ArrayList<>(cfg.excludedModIds))
                .setDefaultValue(List.of())
                .setSaveConsumer(ModConfigScreen::onExcludedListSaved)
                .setTooltip(Component.translatable(LANG_PREFIX + ".option.excludedModIds.tooltip"))
                .build());

        return builder.build();
    }

    /** 保存回调：将 List 转 CopyOnWriteArrayList 保证线程安全 */
    private static void onExcludedListSaved(List<String> list) {
        ConfigManager.getConfig().excludedModIds = new CopyOnWriteArrayList<>(list);
    }

    private static void onSave() {
        ConfigManager.saveConfig();
    }
}
