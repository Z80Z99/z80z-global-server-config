# GlobalServerConfig

![Release](https://img.shields.io/github/v/release/Z80Z99/z80z-globalserverconfig)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen)
![Forge](https://img.shields.io/badge/Forge-47.x-orange)
![License](https://img.shields.io/badge/License-MIT-blue)

A Minecraft Forge 1.20.1 mod that stores Forge server configs in one global directory instead of per-world `serverconfig` folders, so every world shares the same settings.

一个 Minecraft Forge 1.20.1 模组：将每个存档各自独立的服务端配置（serverconfig）统一到全局目录，所有世界共用同一份设置。

Minecraft 1.20.1 · Forge 47.x · Java 17 · Client & server | 客户端与服务端

**[English](#english) | [简体中文](#简体中文)**

## 简体中文

### 核心功能

通过 Mixin 技术，将原本存储在 `saves/<世界>/serverconfig/` 目录下的模组配置文件，自动重定向到全局的 `config/serverconfig/` 目录。

#### 特性

*   **全局重定向**：所有存档共用同一份服务器配置，修改一处生效全局。
*   **自动迁移**：首次启动时自动将旧存档中的配置文件复制到全局目录，保留用户原有设置。
*   **排除列表**：支持通过配置界面添加需要保持每世界独立的模组 ID，使其不受重定向影响。
*   **配置 GUI**：集成 Cloth Config，提供友好的配置界面。

### 安装

1.  安装 [Minecraft Forge 1.20.1 47.x](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html)。
2.  从 [Releases](https://github.com/Z80Z99/z80z-globalserverconfig/releases) 下载最新 `globalserverconfig-forge-<版本>-1.20.1.jar`，放入 `mods` 文件夹。
3.  启动游戏。

Forge 版已通过 jar-in-jar 内置 Cloth Config 11.1.106 和 Architectury 9.2.14，无需另外下载这两个前置。

> **Fabric 不支持。** 仓库中的 `fabric` 模块仅为占位，请勿使用 Fabric 构建产物。

### 使用

1.  在主菜单点击 **Mods**。
2.  找到 **Global Server Config** 并点击 **Config** 按钮。
3.  调整设置并保存。

### 配置说明

模组的配置文件位于 `config/globalserverconfig.json`，也可通过游戏内配置界面修改。

*   `enabled`: 启用/禁用全局重定向功能。
*   `migrationEnabled`: 启用首次启动时的自动迁移功能。
*   `targetSubDir`: 全局配置的子目录名，默认为 `serverconfig`。
*   `excludedModIds`: 排除的模组 ID 列表，列表中的模组配置将保持在每个存档中独立。

### 注意事项

*   **全局基准的来源**：迁移遵循"目标文件已存在则跳过"，因此第一个启动的存档会把自己的 serverconfig 带入全局目录成为基准；之后所有世界读取的都是这份全局配置。
*   **生效时机**：全局 server config 在进入世界时加载。Forge 自带配置文件监听，世界运行中修改 `.toml` 通常可热重载；不支持热重载的模组需要重新进入世界。
*   **回退方式**：在配置界面关闭 `enabled`，或将对应模组 ID 加入 `excludedModIds`（需重启游戏生效），即可恢复该模组每存档独立的 serverconfig。

### 从源码构建

需要 JDK 17。

1.  克隆仓库：`git clone https://github.com/Z80Z99/z80z-globalserverconfig.git`
2.  进入目录：`cd z80z-globalserverconfig`
3.  运行构建：
    *   Windows: `.\gradlew.bat :forge:build`
    *   Linux/macOS: `./gradlew :forge:build`
4.  产物位置：`forge/build/libs/globalserverconfig-forge-<版本>-1.20.1.jar`

### 技术栈

*   **Architectury**: 多平台模组开发框架（本项目仅实现 Forge 端，Fabric 端为不可用占位）。
*   **Mixin**: 核心实现技术，用于拦截 Forge 的配置加载与卸载逻辑。
*   **Cloth Config**: 提供配置 GUI 支持，构建时内置。

### 开源协议

本项目基于 [MIT License](LICENSE) 开源。

---

## English

### Overview

Through Mixin, this mod redirects mod config files that Forge would normally store under `saves/<world>/serverconfig/` to a single global `config/serverconfig/` directory.

#### Features

*   **Global redirection**: all saves share one server config — edit once, applies everywhere.
*   **Auto migration**: on first launch, existing per-world config files are copied to the global directory, preserving your settings.
*   **Exclusion list**: keep chosen mod IDs per-world through the config screen.
*   **Config GUI**: a friendly settings screen built on Cloth Config.

### Installation

1.  Install [Minecraft Forge 1.20.1 47.x](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html).
2.  Download the latest `globalserverconfig-forge-<version>-1.20.1.jar` from [Releases](https://github.com/Z80Z99/z80z-globalserverconfig/releases) and drop it into your `mods` folder.
3.  Launch the game.

The Forge build bundles Cloth Config 11.1.106 and Architectury 9.2.14 via jar-in-jar — no extra downloads needed.

> **Fabric is not supported.** The `fabric` module in this repository is a placeholder only; do not use Fabric build artifacts.

### Usage

1.  Click **Mods** on the main menu.
2.  Find **Global Server Config** and click **Config**.
3.  Adjust the settings and save.

### Configuration

The mod's own config lives at `config/globalserverconfig.json`; every option is also editable in game:

*   `enabled`: enable/disable global redirection.
*   `migrationEnabled`: enable the one-time migration of old per-world configs.
*   `targetSubDir`: subdirectory under `config/` holding the globalized server configs (default `serverconfig`).
*   `excludedModIds`: mod IDs whose server configs stay per-world.

### Notes

*   **Where the baseline comes from**: migration never overwrites existing files, so the first world you launch donates its configs to the global directory; every world (including that one) reads the global copy afterwards.
*   **When changes apply**: global server configs load when a world starts. Forge ships a config file watcher, so editing a `.toml` mid-game usually hot-reloads; mods without reload support need a world re-entry.
*   **Rollback**: turn off `enabled`, or add the mod ID to `excludedModIds` (restart required), to restore per-world server configs for that mod.

### Building from source

Requires JDK 17.

1.  Clone the repository: `git clone https://github.com/Z80Z99/z80z-globalserverconfig.git`
2.  Enter the directory: `cd z80z-globalserverconfig`
3.  Build:
    *   Windows: `.\gradlew.bat :forge:build`
    *   Linux/macOS: `./gradlew :forge:build`
4.  Output: `forge/build/libs/globalserverconfig-forge-<version>-1.20.1.jar`

### Tech stack

*   **Architectury**: multi-loader modding framework (Forge only in this project; the Fabric module is an unusable placeholder).
*   **Mixin**: intercepts Forge's config load and unload logic.
*   **Cloth Config**: powers the settings GUI, bundled at build time.

### License

Released under the [MIT License](LICENSE).
