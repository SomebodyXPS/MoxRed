package com.moxred.core.execution;

import com.moxred.core.actions.BaseAction;
import com.moxred.core.audit.AuditLogger;
import com.moxred.core.telemetry.TelemetryCollector;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

/**
 * Executes individual actions through the MoxRed-Core execution engine
 */
public class ActionExecutor {
    private final ActionRegistry registry;
    private final AuditLogger auditLogger;
    private final TelemetryCollector telemetry;

    public ActionExecutor(ActionRegistry registry, AuditLogger auditLogger, TelemetryCollector telemetry) {
        this.registry = registry;
        this.auditLogger = auditLogger;
        this.telemetry = telemetry;
    }

    /**
     * Execute a single action.
     * @param actionName The action name
     * @param data The action data
     * @param plugin The plugin instance
     * @return The action result
     */
    public ActionResult execute(String actionName, Map<String, Object> data, JavaPlugin plugin) {
        BaseAction action = registry.get(actionName);

        if (action == null) {
            String message = "Unknown action: " + actionName;
            auditLogger.logAction(actionName, false, message);
            return ActionResult.failure(message);
        }

        try {
            ActionContext context = new ActionContext(plugin, data);
            ActionResult result = action.execute(context);

            auditLogger.logAction(actionName, result.isSuccess(), result.getMessage());
            telemetry.recordActionExecuted(actionName);

            return result;
        } catch (Exception e) {
            String message = "Execution error: " + e.getMessage();
            auditLogger.logAction(actionName, false, message);
            return ActionResult.failure(message);
        }
    }
}
