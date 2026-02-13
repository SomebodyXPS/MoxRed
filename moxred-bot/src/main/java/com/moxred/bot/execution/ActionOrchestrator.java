package com.moxred.bot.execution;

import com.moxred.core.MoxRedCore;
import com.moxred.core.execution.ActionResult;

import java.util.Map;

/**
 * Executes actions based on AI recommendations
 * Bridges between AI suggestion and core plugin execution
 */
public class ActionOrchestrator {
    private final MoxRedCore corePlugin;
    private final boolean verboseLogging;

    public ActionOrchestrator(MoxRedCore corePlugin, boolean verboseLogging) {
        this.corePlugin = corePlugin;
        this.verboseLogging = verboseLogging;
    }

    /**
     * Execute an action recommended by the AI
     */
    public ActionResult executeAIAction(String actionName, Map<String, Object> parameters) {
        if (actionName == null || actionName.isEmpty()) {
            return ActionResult.failure("No action specified");
        }

        if (verboseLogging) {
            log("Executing AI action: " + actionName + " with params: " + parameters);
        }

        try {
            // Normalize parameter keys to match core plugin expectations
            Map<String, Object> normalizedParams = normalizeParameters(actionName, parameters);
            
            // Execute the action through the core plugin
            ActionResult result = corePlugin.getActionExecutor().execute(
                actionName,
                normalizedParams,
                corePlugin
            );

            if (verboseLogging) {
                log("Action result: " + result);
            }

            return result;
        } catch (Exception e) {
            logError("Failed to execute action", e);
            return ActionResult.failure("Execution error: " + e.getMessage());
        }
    }

    /**
     * Normalize AI parameters to match action expectations
     * Maps AI-friendly names to core plugin parameter names
     */
    private Map<String, Object> normalizeParameters(String actionName, Map<String, Object> params) {
        Map<String, Object> normalized = new java.util.HashMap<>(params);

        switch (actionName) {
            case "OP_PLAYER":
            case "DEOP_PLAYER":
                // Map playerName to player field if needed
                if (normalized.containsKey("playerName") && !normalized.containsKey("player")) {
                    normalized.put("player", normalized.remove("playerName"));
                }
                break;
            case "GIVE_ITEM":
                // Ensure playerName is present
                if (!normalized.containsKey("playerName") && normalized.containsKey("player")) {
                    normalized.put("playerName", normalized.remove("player"));
                }
                break;
        }

        return normalized;
    }

    /**
     * Check if an action is safe to execute
     */
    public boolean isActionSafe(String actionName) {
        // Whitelist of allowed actions
        return actionName != null && (
            actionName.equals("BROADCAST") ||
            actionName.equals("OP_PLAYER") ||
            actionName.equals("DEOP_PLAYER") ||
            actionName.equals("GET_TPS") ||
            actionName.equals("GIVE_ITEM") ||
            actionName.equals("GIVE_RANK")
        );
    }

    /**
     * Explain what an action will do
     */
    public String explainAction(String actionName, Map<String, Object> parameters) {
        switch (actionName) {
            case "BROADCAST":
                return "📢 Broadcasting: " + parameters.getOrDefault("message", "");
            case "OP_PLAYER":
                return "👑 Granting operator to: " + parameters.getOrDefault("player", "");
            case "DEOP_PLAYER":
                return "❌ Revoking operator from: " + parameters.getOrDefault("player", "");
            case "GIVE_ITEM":
                int quantity = 1;
                Object qty = parameters.get("quantity");
                if (qty instanceof Number) {
                    quantity = ((Number) qty).intValue();
                }
                return "📦 Giving " + quantity + "x " + parameters.getOrDefault("item", "") + " to " + parameters.getOrDefault("playerName", "");
            case "GIVE_RANK":
                return "🎖️ Assigning rank " + parameters.getOrDefault("rank", "") + " to " + parameters.getOrDefault("playerName", "");
            case "GET_TPS":
                return "📊 Fetching server status...";
            default:
                return "Executing: " + actionName;
        }
    }

    private void log(String message) {
        System.out.println("[ActionOrchestrator] " + message);
    }

    private void logError(String context, Exception e) {
        System.err.println("[ActionOrchestrator] " + context + ": " + e.getMessage());
        if (verboseLogging) {
            e.printStackTrace();
        }
    }
}
