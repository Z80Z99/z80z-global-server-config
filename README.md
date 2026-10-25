# GlobalServerConfig

![Release](https://img.shields.io/github/v/release/Z80Z99/z80z-globalserverconfig)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen)
![Forge](https://img.shields.io/badge/Forge-47.x-orange)
![License](https://img.shields.io/badge/License-MIT-blue)

A Minecraft Forge 1.20.1 mod that stores Forge server configs in one global directory instead of per-world `serverconfig` folders, so every world shares the same settings.

Minecraft 1.20.1 · Forge 47.x · Java 17 · Client and server

## 核心功能

通过 Mixin 技术，将原本存储在 `saves/<世界>/serverconfig/` 目录下的模组配置文件，自动重定向到全局的 `config/serverconfig/` 目录。

### 特性

*   **全局重定向**：所有存档共用同一份服务器配置，修改一处生效全局。
*   **自动迁移**：首次启动时自动将旧存档中的配置文件复制到全局目录，保留用户原有设置。
*   **排除列表**：支持通过配置界面添加需要保持每世界独立的模组 ID，使其不受重定向影响。
*   **配置 GUI**：集成 Cloth Config，提供友好的配置界面。

## 安装

1.  安装 [Minecraft Forge 1.20.1 47.x](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html)。
2.  从 [Releases](https://github.com/Z80Z99/z80z-globalserverconfig/releases) 下载最新 `globalserverconfig-forge-<版本>-1.20.1.jar`，放入 `mods` 文件夹。
3.  启动游戏。

Forge 版已通过 jar-in-jar 内置 Cloth Config 11.1.106 和 Architectury 9.2.14，无需另外下载这两个前置。

> **Fabric 不支持。** 仓库中的 `fabric` 模块仅为占位，请勿使用 Fabric 构建产物。

## 使用

1.  在主菜单点击 **Mods**。
2.  找到 **Global Server Config** 并点击 **Config** 按钮。
3.  调整设置并保存。

## 配置说明

模组的配置文件位于 `config/globalserverconfig.json`，也可通过游戏内配置界面修改。

*   `enabled`: 启用/禁用全局重定向功能。
*   `migrationEnabled`: 启用首次启动时的自动迁移功能。
*   `targetSubDir`: 全局配置的子目录名，默认为 `serverconfig`。
*   `excludedModIds`: 排除的模组 ID 列表，列表中的模组配置将保持在每个存档中独立。

## 注意事项

*   **全局基准的来源**：迁移遵循"目标文件已存在则跳过"，因此第一个启动的存档会把自己的 serverconfig 带入全局目录成为基准；之后所有世界读取的都是这份全局配置。
*   **生效时机**：全局 server config 在进入世界时加载。Forge 自带配置文件监听，世界运行中修改 `.toml` 通常可热重载；不支持热重载的模组需要重新进入世界。
*   **回退方式**：在配置界面关闭 `enabled`，或将对应模组 ID 加入 `excludedModIds`（需重启游戏生效），即可恢复该模组每存档独立的 serverconfig。

## 从源码构建

需要 JDK 17。

1.  克隆仓库：`git clone https://github.com/Z80Z99/z80z-globalserverconfig.git`
2.  进入目录：`cd z80z-globalserverconfig`
3.  运行构建：
    *   Windows: `.\gradlew.bat :forge:build`
    *   Linux/macOS: `./gradlew :forge:build`
4.  产物位置：`forge/build/libs/globalserverconfig-forge-<版本>-1.20.1.jar`

## 技术栈

*   **Architectury**: 多平台模组开发框架（本项目仅实现 Forge 端，Fabric 端为不可用占位）。
*   **Mixin**: 核心实现技术，用于拦截 Forge 的配置加载与卸载逻辑。
*   **Cloth Config**: 提供配置 GUI 支持，构建时内置。

## 开源协议

本项目基于 [MIT License](LICENSE) 开源。
