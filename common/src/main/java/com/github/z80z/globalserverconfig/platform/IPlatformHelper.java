package com.github.z80z.globalserverconfig.platform;

import java.nio.file.Path;

/**
 * 平台抽象接口
 * 原理：依赖倒置（DIP），common 通过接口而非具体实现依赖平台
 * 接口隔离（ISP）：仅包含本模组需要的方法
 */
public interface IPlatformHelper {

    Path getConfigDir();

    boolean isClientSide();

    boolean isModLoaded(String modId);

    String getModVersion(String modId);

    /** 当前平台是否支持 server config 重定向（仅 Forge 返回 true） */
    boolean supportsServerConfigRedirect();
}
