package com.moxred.bot.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.EmbedBuilder;
import com.moxred.core.MoxRedCore;
import com.moxred.core.execution.ActionResult;
import com.moxred.bot.config.BotConfig;
import com.moxred.bot.ai.AdvancedAIService;
import com.moxred.bot.data.ServerContextProvider;
import com.moxred.bot.conversation.ConversationManager;
import com.moxred.bot.execution.ActionOrchestrator;

import java.awt.Color;
import java.time.Instant;
import java.util.*;

/**
 * Handles Discord events and AI-driven command processing
 * Converts natural language messages to actions via AI
 */
public class DiscordEventHandler extends ListenerAdapter {
    private final MoxRedCore corePlugin;
    private final BotConfig config;
    private final String adminRoleId;
    private final boolean verboseLogging;
    private final AdvancedAIService aiService;
    private final ServerContextProvider serverContext;
    private final ConversationManager conversationManager;
    private final ActionOrchestrator actionOrchestrator;

    public DiscordEventHandler(MoxRedCore corePlugin, BotConfig config) {
        this.corePlugin = corePlugin;
        this.config = config;
        this.adminRoleId = config.getAdminRoleId();
        this.verboseLogging = config.isVerboseLogging();
        
        // Initialize AI and supporting services
        this.serverContext = new ServerContextProvider(corePlugin);
        this.conversationManager = new ConversationManager();
        this.aiService = new AdvancedAIService(
            config.getGeminiApiKey(),
            serverContext,
            conversationManager,
            verboseLogging
        );
        this.actionOrchestrator = new ActionOrchestrator(corePlugin, verboseLogging);
    }

    /**
     * Handle all incoming messages for natural language processing
     * Messages can be regular messages or commands
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore bot messages
        if (event.getAuthor().isBot()) {
            return;
        }

        // Only respond in guild channels (not DMs)
        if (!event.isFromGuild()) {
            return;
        }

        // Check authorization
        if (event.getMember() == null) {
            return;
        }

        boolean hasAdminRole = event.getMember().getRoles().stream()
            .anyMatch(role -> role.getId().equals(adminRoleId));

        if (!hasAdminRole) {
            return;
        }

        String messageContent = event.getMessage().getContentRaw().trim();

        // Skip empty messages
        if (messageContent.isEmpty()) {
            return;
        }

        // Skip slash commands (handled separately)
        if (messageContent.startsWith("/")) {
            return;
        }

        // Require bot mention - only process if bot is mentioned
        String botMentionPattern = "<@!?" + event.getJDA().getSelfUser().getId() + ">";
        if (!messageContent.contains(event.getJDA().getSelfUser().getAsMention()) && 
            !messageContent.matches(".*<@!?" + event.getJDA().getSelfUser().getId() + ">.*")) {
            return;
        }

        // Remove bot mention
        String cleanMessage = messageContent.replaceAll("<@!?" + event.getJDA().getSelfUser().getId() + ">", "").trim();

        if (cleanMessage.isEmpty()) {
            return;
        }

        // Show typing indicator
        event.getChannel().sendTyping().queue();

        // Process message asynchronously
        processMessage(event, cleanMessage);
    }

    /**
     * Process natural language message through AI
     */
    private void processMessage(MessageReceivedEvent event, String userMessage) {
        try {
            String userId = event.getAuthor().getId();
            String userName = event.getAuthor().getName();

            log("Message from " + userName + ": " + userMessage);

            // Get AI response
            AdvancedAIService.AIResponse aiResponse = aiService.processCommand(userId, userMessage);

            if (!aiResponse.success) {
                sendErrorMessage(event, "Failed to process request", aiResponse.message);
                return;
            }

            // Display AI understanding
            String action = aiResponse.getAction();
            Map<String, Object> parameters = aiResponse.getParameters();
            double confidence = aiResponse.getConfidence();

            // If AI recommends an action
            if (action != null && !action.isEmpty() && !action.equals("null")) {
                if (!actionOrchestrator.isActionSafe(action)) {
                    sendErrorMessage(event, "Unknown Action", "The action '" + action + "' is not recognized.");
                    return;
                }

                // Show what we're about to do
                String explanation = actionOrchestrator.explainAction(action, parameters);
                event.getChannel().sendMessageEmbeds(
                    createPendingEmbed("Processing", explanation, confidence)
                ).queue();

                // Execute the action
                ActionResult result = actionOrchestrator.executeAIAction(action, parameters);

                // Send result
                if (result.isSuccess()) {
                    event.getChannel().sendMessageEmbeds(
                        createSuccessEmbed("Success ✅", result.getMessage())
                    ).queue();
                } else {
                    event.getChannel().sendMessageEmbeds(
                        createErrorEmbed("Action Failed ❌", result.getMessage())
                    ).queue();
                }
            } else {
                // Just a response, no action needed
                event.getChannel().sendMessageEmbeds(
                    createResponseEmbed("MoxRed AI", aiResponse.message)
                ).queue();
            }

        } catch (Exception e) {
            logError("Failed to process message", e);
            sendErrorMessage(event, "Error", "Failed to process your message: " + e.getMessage());
        }
    }

    /**
     * Handle legacy slash commands (for backward compatibility)
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!isAuthorized(event)) {
            event.reply("You don't have permission to use this command").setEphemeral(true).queue();
            return;
        }

        String command = event.getName();
        event.deferReply().queue();

        switch (command) {
            case "help":
                handleHelp(event);
                break;
            case "context":
                handleContext(event);
                break;
            case "clear":
                handleClearConversation(event);
                break;
            default:
                event.getHook().editOriginalEmbeds(
                    createErrorEmbed("Unknown Command", "Use natural language messages instead!")
                ).queue();
        }
    }

    /**
     * Show help information
     */
    private void handleHelp(SlashCommandInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("MoxRed AI Help")
            .setDescription("MoxRed is an AI-powered server management system. Just send natural language messages!")
            .addField("Examples", 
                "📢 \"Broadcast the server is rebooting in 5 minutes\"\n" +
                "👑 \"Op the player Steve\"\n" +
                "📦 \"Give Alex 10 diamonds\"\n" +
                "🎖️ \"Make Mike a builder\"\n" +
                "📊 \"What's the server status?\"",
                false)
            .addField("Commands",
                "/help - This help message\n" +
                "/context - See current server state\n" +
                "/clear - Clear conversation history",
                false)
            .setFooter("MoxRed AI v1.0")
            .setColor(Color.BLUE)
            .setTimestamp(Instant.now());

        event.getHook().editOriginalEmbeds(embed.build()).queue();
    }

    /**
     * Show current server context
     */
    private void handleContext(SlashCommandInteractionEvent event) {
        String contextText = serverContext.getServerContextAsText();
        String[] lines = contextText.split("\n");
        
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (sb.length() + line.length() > 4000) break; // Discord embed limit
            sb.append(line).append("\n");
        }

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("Server Context")
            .setDescription("```\n" + sb.toString() + "\n```")
            .setFooter("MoxRed")
            .setColor(Color.CYAN)
            .setTimestamp(Instant.now());

        event.getHook().editOriginalEmbeds(embed.build()).queue();
    }

    /**
     * Clear user's conversation history
     */
    private void handleClearConversation(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();
        conversationManager.clearConversation(userId);
        
        event.getHook().editOriginalEmbeds(
            createSuccessEmbed("Cleared", "Your conversation history has been cleared.")
        ).queue();
    }

    /**
     * Register slash commands with Discord
     */
    public static void registerCommands(JDA jda) {
        jda.updateCommands()
            .addCommands(
                Commands.slash("help", "Show help information"),
                Commands.slash("context", "View current server state"),
                Commands.slash("clear", "Clear conversation history")
            )
            .queue();
    }

    /**
     * Check if user has admin role
     */
    private boolean isAuthorized(SlashCommandInteractionEvent event) {
        if (event.getMember() == null) return false;
        return event.getMember().getRoles().stream()
            .anyMatch(role -> role.getId().equals(adminRoleId));
    }

    /**
     * Create a success embed
     */
    private MessageEmbed createSuccessEmbed(String title, String description) {
        return new EmbedBuilder()
            .setTitle(title)
            .setDescription(description)
            .setColor(Color.GREEN)
            .setTimestamp(Instant.now())
            .setFooter("MoxRed")
            .build();
    }

    /**
     * Create an error embed
     */
    private MessageEmbed createErrorEmbed(String title, String description) {
        return new EmbedBuilder()
            .setTitle(title)
            .setDescription(description)
            .setColor(Color.RED)
            .setTimestamp(Instant.now())
            .setFooter("MoxRed")
            .build();
    }

    /**
     * Create a response embed for AI messages
     */
    private MessageEmbed createResponseEmbed(String title, String description) {
        return new EmbedBuilder()
            .setTitle(title)
            .setDescription(description)
            .setColor(Color.YELLOW)
            .setTimestamp(Instant.now())
            .setFooter("MoxRed")
            .build();
    }

    /**
     * Create a pending/processing embed
     */
    private MessageEmbed createPendingEmbed(String title, String description, double confidence) {
        String confidenceBar = confidence >= 0.9 ? "🟢" : confidence >= 0.7 ? "🟡" : "🔴";
        return new EmbedBuilder()
            .setTitle(title)
            .setDescription(description)
            .addField("Confidence", confidenceBar + " " + (int)(confidence * 100) + "%", true)
            .setColor(Color.CYAN)
            .setTimestamp(Instant.now())
            .setFooter("MoxRed")
            .build();
    }

    /**
     * Send an error message to the user
     */
    private void sendErrorMessage(MessageReceivedEvent event, String title, String message) {
        event.getChannel().sendMessageEmbeds(createErrorEmbed(title, message)).queue();
    }

    /**
     * Log message
     */
    private void log(String message) {
        System.out.println("[DiscordEventHandler] " + message);
    }

    /**
     * Log error
     */
    private void logError(String context, Exception e) {
        System.err.println("[DiscordEventHandler] " + context + ": " + e.getMessage());
        if (verboseLogging) {
            e.printStackTrace();
        }
    }
}
