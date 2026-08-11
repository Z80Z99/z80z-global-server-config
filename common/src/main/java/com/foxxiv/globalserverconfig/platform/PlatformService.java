package com.foxxiv.globalserverconfig.platform;

/**
 * 平台服务定位器
 * 原理：由各平台入口在初始化时注入 IPlatformHelper 实现
 * 线程安全：helper 写入后只读，单次注入保证可见性
 */
public final class PlatformService {
    private static IPlatformHelper helper;

    private PlatformService() {}

    public static IPlatformHelper get() {
        if (helper == null) {
            throw new IllegalStateException("PlatformHelper has not been set! Call setHelper() in mod initializer.");
        }
        return helper;
    }

    public static void setHelper(IPlatformHelper platformHelper) {
        if (helper != null) {
            throw new IllegalStateException("PlatformHelper has already been set!");
        }
        helper = platformHelper;
    }
}
