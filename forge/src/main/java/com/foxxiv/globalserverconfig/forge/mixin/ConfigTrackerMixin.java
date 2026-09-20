package com.foxxiv.globalserverconfig.forge.mixin;

import com.foxxiv.globalserverconfig.forge.redirect.ServerConfigLifecycle;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.nio.file.Path;

/** Hook the game-layer caller; FML's ConfigTracker bypasses game mixins. */
@Mixin(value = ServerLifecycleHooks.class, remap = false)
public abstract class ConfigTrackerMixin {
    @Redirect(method = "handleServerAboutToStart", at = @At(value = "INVOKE",
            target = "Lnet/minecraftforge/fml/config/ConfigTracker;loadConfigs(Lnet/minecraftforge/fml/config/ModConfig$Type;Ljava/nio/file/Path;)V"))
    private static void gsc$load(ConfigTracker tracker, ModConfig.Type type, Path path) {
        ServerConfigLifecycle.load(tracker, type, path);
    }

    @Redirect(method = "handleServerStopped", at = @At(value = "INVOKE",
            target = "Lnet/minecraftforge/fml/config/ConfigTracker;unloadConfigs(Lnet/minecraftforge/fml/config/ModConfig$Type;Ljava/nio/file/Path;)V"))
    private static void gsc$unload(ConfigTracker tracker, ModConfig.Type type, Path path) {
        ServerConfigLifecycle.unload(tracker, type, path);
    }
}