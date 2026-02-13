package com.moxred.core.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Core configuration manager.
 */
public class CoreConfig {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    // Feature flags
    private boolean enableWorkflowMode;
    private boolean enableRawConsole;

    public CoreConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    /**
     * Load configuration from config.yml
     */
    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        // Feature settings
        enableWorkflowMode = config.getBoolean("features.enableWorkflowMode", true);
        enableRawConsole = config.getBoolean("features.enableRawConsole", false);
    }

    // Getters
    public boolean isEnableWorkflowMode() {
        return enableWorkflowMode;
    }

    public boolean isEnableRawConsole() {
        return enableRawConsole;
    }

    // Legacy security/network methods (kept for compatibility with SecurityManager)
    // These are no longer used in the local plugin architecture
    public int getNonceCacheSize() {
        return 1000; // Default cache size
    }

    public int getProtocolVersion() {
        return 1; // Default protocol version
    }

    public String getSharedSecret() {
        return config.getString("security.sharedSecret", ""); // Unused in local architecture
    }

    public int getTimestampToleranceSeconds() {
        return 300; // Default 5 minutes tolerance
    }
}
