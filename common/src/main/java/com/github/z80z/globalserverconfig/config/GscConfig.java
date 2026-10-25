package com.github.z80z.globalserverconfig.config;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 模组配置数据类
 * 原理：使用 Gson 序列化为 JSON 持久化
 * 线程安全：excludedModIds 使用 CopyOnWriteArrayList 保证遍历安全
 */
public class GscConfig {
    /** 配置版本号，便于后续升级 */
    public int configVersion = 1;

    /** 总开关：是否启用 server config 全局化重定向 */
    public boolean enabled = true;

    /** 是否启用首次启动迁移（saves/<world>/serverconfig → config/serverconfig） */
    public boolean migrationEnabled = true;

    /** 全局 server config 存放的子目录名（相对于 config/） */
    public String targetSubDir = "serverconfig";

    /** 排除列表：这些 mod 的 server config 不被全局化，仍每个世界独立 */
    public List<String> excludedModIds = new CopyOnWriteArrayList<>();
}
