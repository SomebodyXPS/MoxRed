package com.moxred.bot.ai;

import java.util.*;

/**
 * Orchestrates natural language command processing
 * Bridges between Discord messages and core plugin actions
 */
public class NaturalLanguageParser {
    private final GeminiService gemini;
    private final boolean verboseLogging;

    public NaturalLanguageParser(GeminiService gemini, boolean verboseLogging) {
        this.gemini = gemini;
        this.verboseLogging = verboseLogging;
    }

    /**
     * Parse a Discord message into a command intent
     */
    public CommandParseResult parseMessage(String userId, String authorName, String message) {
        try {
            if (message == null || message.trim().isEmpty()) {
                return new CommandParseResult(null, "Empty message");
            }

            // Remove @ mentions and clean up message
            String cleanMessage = message.replaceAll("<@[!&]?\\d+>", "").trim();
            if (cleanMessage.isEmpty()) {
                return new CommandParseResult(null, "Message contains only mentions");
            }

            if (verboseLogging) {
                log("Parsing message from " + authorName + ": " + cleanMessage);
            }

            // Call Gemini to parse
            GeminiService.ParsedCommand parsed = gemini.parseCommand(cleanMessage);

            if (!parsed.isSuccess()) {
                return new CommandParseResult(null, parsed.getErrorMessage());
            }

            GeminiService.CommandIntent intent = parsed.intent;
            if (intent == null) {
                return new CommandParseResult(null, "Could not extract command intent");
            }

            // Validate the action
            if (!isValidAction(intent.getAction())) {
                return new CommandParseResult(null, "Unknown action: " + intent.getAction());
            }

            // Validate data makes sense for the action
            String validationError = validateActionData(intent.getAction(), intent.getData());
            if (validationError != null) {
                return new CommandParseResult(null, validationError);
            }

            if (verboseLogging) {
                log("Successfully parsed command: action=" + intent.getAction() + 
                    ", data=" + intent.getData());
            }

            return new CommandParseResult(intent, null);

        } catch (Exception e) {
            logError("Failed to parse message", e);
            return new CommandParseResult(null, "Error: " + e.getMessage());
        }
    }

    /**
     * Check if action name is valid
     */
    private boolean isValidAction(String action) {
        if (action == null) return false;

        return action.equals("BROADCAST") ||
               action.equals("OP_PLAYER") ||
               action.equals("DEOP_PLAYER") ||
               action.equals("GIVE_ITEM") ||
               action.equals("GIVE_RANK") ||
               action.equals("GET_TPS");
    }

    /**
     * Validate action data contains required fields
     */
    private String validateActionData(String action, Map<String, Object> data) {
        if (data == null) {
            if (action.equals("GET_TPS")) {
                return null; // GET_TPS takes no data
            }
            return "Missing action data";
        }

        switch (action) {
            case "BROADCAST":
                if (!data.containsKey("message")) {
                    return "BROADCAST requires 'message' field";
                }
                if (!(data.get("message") instanceof String)) {
                    return "BROADCAST message must be a string";
                }
                return null;

            case "OP_PLAYER":
            case "DEOP_PLAYER":
                if (!data.containsKey("playerName")) {
                    return action + " requires 'playerName' field";
                }
                if (!(data.get("playerName") instanceof String)) {
                    return action + " playerName must be a string";
                }
                return null;

            case "GIVE_ITEM":
                if (!data.containsKey("playerName")) {
                    return "GIVE_ITEM requires 'playerName' field";
                }
                if (!data.containsKey("item")) {
                    return "GIVE_ITEM requires 'item' field";
                }
                // quantity and enchantments are optional
                return null;

            case "GIVE_RANK":
                if (!data.containsKey("playerName")) {
                    return "GIVE_RANK requires 'playerName' field";
                }
                if (!data.containsKey("rank")) {
                    return "GIVE_RANK requires 'rank' field";
                }
                return null;

            case "GET_TPS":
                return null; // No required fields

            default:
                return "Unknown action: " + action;
        }
    }

    /**
     * Log message
     */
    private void log(String message) {
        System.out.println("[NaturalLanguageParser] " + message);
    }

    /**
     * Log error
     */
    private void logError(String context, Exception e) {
        System.err.println("[NaturalLanguageParser] " + context + ": " + e.getMessage());
        if (verboseLogging) {
            e.printStackTrace();
        }
    }

    /**
     * Result of parsing a message
     */
    public static class CommandParseResult {
        public final GeminiService.CommandIntent intent;
        public final String error;

        public CommandParseResult(GeminiService.CommandIntent intent, String error) {
            this.intent = intent;
            this.error = error;
        }

        public boolean isSuccess() {
            return error == null && intent != null;
        }

        public String getErrorMessage() {
            return error != null ? error : "Unknown error";
        }
    }
}
