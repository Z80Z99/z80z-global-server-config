# GlobalServerConfig

一个 Minecraft Forge 1.20.1 模组，旨在解决 Forge 服务器配置（Server Config）在每个存档中独立存储的问题。

## 核心功能

通过 Mixin 技术，将原本存储在 `saves/<世界>/serverconfig/` 目录下的模组配置文件，自动重定向到全局的 `config/serverconfig/` 目录。

### 特性

*   **全局重定向**：所有存档共用同一份服务器配置，修改一处生效全局。
*   **自动迁移**：首次启动时自动将旧存档中的配置文件复制到全局目录，保留用户原有设置。
*   **排除列表**：支持通过配置界面添加需要保持每世界独立的模组 ID，使其不受重定向影响。
*   **配置 GUI**：集成 Cloth Config，提供友好的中文配置界面。

## 安装与使用

### 玩家使用

1.  **下载模组**：从 Releases 页面下载最新的 `globalserverconfig-forge-x.x.x-1.20.1.jar`。
2.  **安装**：将下载的 jar 文件放入你的 Minecraft 实例的 `mods` 文件夹中。
3.  **启动游戏**：运行 Forge 版本的 Minecraft。
4.  **配置**：
    *   在主菜单点击 **Mods**。
    *   找到 **Global Server Config** 并点击 **Config** 按钮。
    *   调整设置并保存。

### 开发者构建

**前置要求**：
*   Java 17 或更高版本
*   Gradle Wrapper (项目自带)

**构建步骤**：

1.  克隆仓库：`git clone <repository-url>`
2.  进入目录：`cd 1.20.1-GlobalServerConfig`
3.  运行构建：
    *   Windows: `.\gradlew.bat :forge:build`
    *   Linux/macOS: `./gradlew :forge:build`
4.  产物位置：`forge/build/libs/` 目录下的 jar 文件。

## 配置说明

模组的配置文件位于 `config/globalserverconfig.json`，也可通过游戏内配置界面修改。

*   `enabled`: 启用/禁用全局重定向功能。
*   `migrationEnabled`: 启用首次启动时的自动迁移功能。
*   `targetSubDir`: 全局配置的子目录名，默认为 `serverconfig`。
*   `excludedModIds`: 排除的模组 ID 列表，列表中的模组配置将保持在每个存档中独立。

## 技术栈

*   **Architectury**: 多平台模组开发框架（本项目主要实现 Forge 端，Fabric 端仅作占位）。
*   **Mixin**: 核心实现技术，用于拦截 Forge 的配置加载与卸载逻辑。
*   **Cloth Config**: 提供配置 GUI 支持。

## 开源协议

本项目基于 [MIT License](LICENSE) 开源。
