package com.foxxiv.globalserverconfig.forge.redirect;

import com.foxxiv.globalserverconfig.GlobalServerConfig;
import com.foxxiv.globalserverconfig.config.ConfigManager;
import com.foxxiv.globalserverconfig.forge.migration.ServerConfigMigrator;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.Map;

/** Retains Forge's own validation, events and file watcher lifecycle. */
public final class ServerConfigLifecycle {
    private static final Map<ModConfig, Path> LOADED_PATHS = new IdentityHashMap<>();
    private ServerConfigLifecycle() {}

    // FML is outside the transforming game class loader; Mixin accessors cannot reach it.
    private static final class ForgeMethods {
        static final Method OPEN = find("openConfig");
        static final Method CLOSE = find("closeConfig");
        private static Method find(String name) {
            try {
                Method method = ConfigTracker.class.getDeclaredMethod(name, ModConfig.class, Path.class);
                method.setAccessible(true);
                return method;
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Unsupported Forge ConfigTracker API", e);
            }
        }
    }

    public static void load(ConfigTracker tracker, ModConfig.Type type, Path worldPath) {
        if (!ConfigRedirector.shouldRedirect(type)) {
            tracker.loadConfigs(type, worldPath);
            return;
        }
        if (!LOADED_PATHS.isEmpty()) throw new IllegalStateException("Previous server configs were not unloaded");
        if (ConfigManager.getConfig().migrationEnabled) {
            ServerConfigMigrator.migrate(worldPath, ConfigManager.getGlobalServerConfigDir());
        }
        for (ModConfig config : tracker.configSets().get(type)) {
            Path path = ConfigRedirector.resolvePath(config, worldPath);
            // Record first: a loading listener may throw after the file has been opened.
            LOADED_PATHS.put(config, path);
            try {
                invoke(ForgeMethods.OPEN, tracker, config, path);
                GlobalServerConfig.LOGGER.info("Loaded server config {} from {}", config.getModId(), path.resolve(config.getFileName()));
            } catch (RuntimeException | Error failure) {
                try { unload(tracker, type, worldPath); }
                catch (RuntimeException | Error cleanup) { failure.addSuppressed(cleanup); }
                throw failure;
            }
        }
    }

    public static void unload(ConfigTracker tracker, ModConfig.Type type, Path worldPath) {
        if (type != ModConfig.Type.SERVER || LOADED_PATHS.isEmpty()) {
            tracker.unloadConfigs(type, worldPath);
            return;
        }
        // Settings may change while a world is open. Always close using the loaded path.
        RuntimeException failure = null;
        try {
            for (Map.Entry<ModConfig, Path> entry : LOADED_PATHS.entrySet()) {
                try { invoke(ForgeMethods.CLOSE, tracker, entry.getKey(), entry.getValue()); }
                catch (RuntimeException e) {
                    if (failure == null) failure = e;
                    else failure.addSuppressed(e);
                }
            }
        } finally { LOADED_PATHS.clear(); }
        if (failure != null) throw failure;
    }

    private static void invoke(Method method, ConfigTracker tracker, ModConfig config, Path path) {
        try { method.invoke(tracker, config, path); }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtime) throw runtime;
            if (cause instanceof Error error) throw error;
            throw new IllegalStateException("Forge config operation failed: " + config.getFileName(), cause);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot access Forge config lifecycle", e);
        }
    }
}