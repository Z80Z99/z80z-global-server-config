package com.github.z80z.globalserverconfig.forge;

/** Migration runs in ServerConfigLifecycle before loading.
 * ServerAboutToStartEvent is posted AFTER config loading.
 */
public final class ForgeEventHandler {
    private ForgeEventHandler() {}
}