package com.moxred.bot.config;

import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Bot configuration management.
 * Loads and manages Discord bot configuration from config.yml
 */
public class BotConfig {
    private String botToken;
    private String adminRoleId;
    private boolean verboseLogging;
    private String geminiApiKey;
    private boolean aiEnabled;
    private File configFile;

    private static final String CONFIG_FILENAME = "config.yml";
    private static final String DEFAULT_CONFIG = """
            # MoxRed Bot Configuration
            
            # Discord Bot Token (get from Discord Developer Portal)
            # https://discord.com/developers/applications
            botToken: "YOUR_DISCORD_BOT_TOKEN"
            
            # Admin Role ID (users with this role can execute commands)
            # Right-click role in Discord > Copy Role ID
            adminRoleId: "YOUR_ADMIN_ROLE_ID"
            
            # AI Integration (Gemini 2.5 Flash)
            # Enables natural language command processing
            ai:
              # Enable or disable AI features
              enabled: true
              
              # Google Gemini API Key
              # Get from: https://aistudio.google.com/apikey
              # Make sure Gemini 2.5 Flash is enabled in your API console
              geminiApiKey: "YOUR_GEMINI_API_KEY"
            
            # Enable verbose logging for debugging
            verboseLogging: false
            """;

    public BotConfig(File dataFolder) {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.configFile = new File(dataFolder, CONFIG_FILENAME);
        createDefaultConfigIfNeeded();
        load();
    }

    public BotConfig() {
        this(new File("."));
    }

    /**
     * Create default config file if it doesn't exist
     */
    private void createDefaultConfigIfNeeded() {
        if (!configFile.exists()) {
            try {
                Files.write(configFile.toPath(), DEFAULT_CONFIG.getBytes());
                System.out.println("[MoxRed Bot] Created default config.yml - please configure before running");
            } catch (IOException e) {
                System.err.println("[MoxRed Bot] Failed to create default config: " + e.getMessage());
            }
        }
    }

    /**
     * Load configuration from YAML file
     */
    @SuppressWarnings("unchecked")
    private void load() {
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(new FileInputStream(configFile));

            if (data == null) {
                throw new RuntimeException("Invalid or empty config.yml");
            }

            this.botToken = getStringValue(data, "botToken", "YOUR_DISCORD_BOT_TOKEN");
            this.adminRoleId = getStringValue(data, "adminRoleId", "YOUR_ADMIN_ROLE_ID");
            this.verboseLogging = getBooleanValue(data, "verboseLogging", false);

            // Load AI config
            Map<String, Object> aiConfig = (Map<String, Object>) data.getOrDefault("ai", new HashMap<>());
            this.aiEnabled = getBooleanValue(aiConfig, "enabled", false);
            this.geminiApiKey = getStringValue(aiConfig, "geminiApiKey", "YOUR_GEMINI_API_KEY");

            validateConfiguration();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.yml: " + e.getMessage(), e);
        }
    }

    /**
     * Validate that critical configuration is set
     */
    private void validateConfiguration() {
        if (botToken.contains("YOUR_DISCORD_BOT_TOKEN")) {
            throw new RuntimeException("botToken must be configured in config.yml");
        }
        if (adminRoleId.contains("YOUR_ADMIN_ROLE_ID")) {
            throw new RuntimeException("adminRoleId must be configured in config.yml");
        }
        if (aiEnabled && geminiApiKey.contains("YOUR_GEMINI_API_KEY")) {
            throw new RuntimeException("geminiApiKey must be configured if ai.enabled is true");
        }
    }

    /**
     * Helper to get string value from config map
     */
    @SuppressWarnings("unchecked")
    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * Helper to get integer value from config map
     */
    private int getIntValue(Map<String, Object> map, String key, int defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    /**
     * Helper to get long value from config map
     */
    private long getLongValue(Map<String, Object> map, String key, long defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return defaultValue;
    }

    /**
     * Helper to get boolean value from config map
     */
    private boolean getBooleanValue(Map<String, Object> map, String key, boolean defaultValue) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    // Getters
    public String getBotToken() {
        return botToken;
    }

    public String getAdminRoleId() {
        return adminRoleId;
    }

    public boolean isVerboseLogging() {
        return verboseLogging;
    }

    public boolean isAiEnabled() {
        return aiEnabled;
    }

    public String getGeminiApiKey() {
        return geminiApiKey;
    }
}
