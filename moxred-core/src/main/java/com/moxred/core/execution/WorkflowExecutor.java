package com.moxred.core.execution;

import com.moxred.core.actions.BaseAction;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Executes workflows (sequences of actions) in MoxRed-Core
 */
public class WorkflowExecutor {
    private final ActionRegistry registry;

    public WorkflowExecutor(ActionRegistry registry) {
        this.registry = registry;
    }

    /**
     * Execute a series of workflow steps.
     * @param steps The workflow steps
     * @param plugin The plugin instance
     * @return List of results for each step
     */
    public List<ActionResult> executeSteps(List<Map<String, Object>> steps, JavaPlugin plugin) {
        List<ActionResult> results = new ArrayList<>();

        if (steps == null || steps.isEmpty()) {
            results.add(ActionResult.failure("No steps provided"));
            return results;
        }

        for (Map<String, Object> step : steps) {
            if (step == null) {
                results.add(ActionResult.failure("Null step encountered"));
                break;
            }

            String actionName = (String) step.get("action");
            if (actionName == null || actionName.isEmpty()) {
                results.add(ActionResult.failure("Step missing action name"));
                break;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) step.get("data");
            if (data == null) {
                data = new java.util.HashMap<>();
            }

            BaseAction action = registry.get(actionName);
            if (action == null) {
                results.add(ActionResult.failure("Unknown action: " + actionName));
                break;
            }

            try {
                ActionContext context = new ActionContext(plugin, data);
                ActionResult result = action.execute(context);
                results.add(result);

                // Stop on first failure
                if (!result.isSuccess()) {
                    break;
                }
            } catch (Exception e) {
                results.add(ActionResult.failure("Execution error: " + e.getMessage()));
                break;
            }
        }

        return results;
    }
}
