package com.foxxiv.globalserverconfig.forge.migration;

import com.foxxiv.globalserverconfig.GlobalServerConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * 首次启动迁移服务
 * 原理：把 saves/<world>/serverconfig/ 下的 .toml（含子目录）复制到 config/<targetSubDir>/
 *       保持相对路径结构，子目录配置（如 valkyrienskies/clockwork/server.toml）原样迁移
 * 冲突策略：目标已存在则跳过（不覆盖用户修改）
 * 异常处理：单个文件复制失败仅记日志，不中断其他文件
 * 资源管理：try-with-resources 关闭 Stream<Path> 防文件句柄泄漏
 */
public final class ServerConfigMigrator {

    private static final String TOML_SUFFIX = ".toml";

    private ServerConfigMigrator() {}

    /**
     * 执行迁移（递归处理子目录）
     * @param sourceDir 源目录（世界 serverconfig）
     * @param targetDir 目标目录（全局 config 子目录）
     */
    public static void migrate(Path sourceDir, Path targetDir) {
        if (sourceDir == null || targetDir == null) {
            return;
        }
        if (!Files.isDirectory(sourceDir)) {
            GlobalServerConfig.LOGGER.debug("Migration source not exist: {}", sourceDir);
            return;
        }
        if (!ensureTargetDir(targetDir)) {
            return;
        }
        // Files.walk 递归遍历所有层级，保证子目录里的配置（如
        // valkyrienskies/clockwork/server.toml）也能被迁移
        try (Stream<Path> files = Files.walk(sourceDir)) {
            files.filter(Files::isRegularFile)
                 .filter(ServerConfigMigrator::isTomlFile)
                 .forEach(p -> migrateFile(p, sourceDir, targetDir));
        } catch (IOException e) {
            GlobalServerConfig.LOGGER.error("Failed to walk source dir {}", sourceDir, e);
        }
    }

    private static boolean ensureTargetDir(Path targetDir) {
        try {
            Files.createDirectories(targetDir);
            return true;
        } catch (IOException e) {
            GlobalServerConfig.LOGGER.error("Failed to create target dir {}", targetDir, e);
            return false;
        }
    }

    private static boolean isTomlFile(Path p) {
        String name = p.getFileName().toString();
        return name.endsWith(TOML_SUFFIX);
    }

    /**
     * 迁移单个文件，保持相对路径结构
     * 原理：用源文件相对 sourceDir 的路径作为在 targetDir 下的相对路径，
     *      子目录结构原样保留
     */
    private static void migrateFile(Path source, Path sourceRoot, Path targetRoot) {
        Path relative = sourceRoot.relativize(source);
        Path target = targetRoot.resolve(relative);
        if (Files.exists(target)) {
            GlobalServerConfig.LOGGER.debug("Skip migrating {}, target exists", relative);
            return;
        }
        try {
            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES);
            GlobalServerConfig.LOGGER.info("Migrated {} -> {}", relative, target);
        } catch (IOException e) {
            GlobalServerConfig.LOGGER.error("Failed to migrate {}", source, e);
        }
    }
}
