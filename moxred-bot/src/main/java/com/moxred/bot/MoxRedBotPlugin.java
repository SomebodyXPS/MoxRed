package com.moxred.bot;

import com.moxred.bot.config.BotConfig;
import com.moxred.bot.discord.DiscordEventHandler;
import com.moxred.core.MoxRedCore;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;

/**
 * MoxRed-Bot: AI-Powered Control Layer
 * Discord integration with natural language processing and server management
 */
public class MoxRedBotPlugin extends JavaPlugin {
    private JDA jda;
    private MoxRedCore corePlugin;
    private BotConfig config;

    @Override
    public void onEnable() {
        try {
            log("Starting MoxRed-Bot v" + getDescription().getVersion());

            // Load configuration
            saveDefaultConfig();
            config = new BotConfig(getDataFolder());

            // Get core plugin
            if (!initializeCorePlugin()) {
                log(Level.SEVERE, "Failed to access MoxRed-Core plugin!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            log("Connected to MoxRed-Core plugin");

            // Initialize Discord bot
            try {
                initializeDiscordBot();
            } catch (LoginException e) {
                log(Level.SEVERE, "Failed to login to Discord: " + e.getMessage());
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            log("MoxRed-Bot enabled successfully!");
            log("Listening for natural language messages in Discord...");

        } catch (Exception e) {
            log(Level.SEVERE, "Failed to enable MoxRed-Bot: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        try {
            log("Shutting down MoxRed-Bot...");

            if (jda != null) {
                jda.shutdown();
            }

            log("MoxRed-Bot disabled");
        } catch (Exception e) {
            log(Level.SEVERE, "Error during shutdown: " + e.getMessage());
        }
    }

    /**
     * Get reference to core plugin
     */
    private boolean initializeCorePlugin() {
        org.bukkit.plugin.Plugin plugin = getServer().getPluginManager().getPlugin("MoxRed-Core");
        
        if (plugin == null) {
            log(Level.SEVERE, "MoxRed core plugin not found! Make sure MoxRed loads before MoxRed-Bot");
            return false;
        }
        
        if (!(plugin instanceof MoxRedCore)) {
            log(Level.SEVERE, "MoxRed plugin is not an instance of MoxRedCore: " + plugin.getClass().getName());
            return false;
        }

        corePlugin = (MoxRedCore) plugin;
        log("Successfully connected to MoxRed-Core");
        return true;
    }

    /**
     * Initialize Discord bot with JDA
     */
    private void initializeDiscordBot() throws LoginException {
        log("Initializing Discord bot...");

        jda = JDABuilder.createDefault(config.getBotToken())
            .enableIntents(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS
            )
            .addEventListeners(new DiscordEventHandler(corePlugin, config))
            .build();

        // Wait for ready
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LoginException("Interrupted while waiting for JDA to be ready");
        }

        // Register slash commands
        DiscordEventHandler.registerCommands(jda);

        log("Discord bot ready as: " + jda.getSelfUser().getAsTag());
    }

    /**
     * Get the core plugin instance
     */
    public MoxRedCore getCorePlugin() {
        return corePlugin;
    }

    /**
     * Log message
     */
    private void log(String message) {
        getLogger().info(message);
    }

    /**
     * Log message with level
     */
    private void log(Level level, String message) {
        getLogger().log(level, message);
    }
}
