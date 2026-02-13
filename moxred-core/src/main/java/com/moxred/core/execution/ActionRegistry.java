package com.moxred.core.execution;

import com.moxred.core.actions.BaseAction;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for all available actions in MoxRed-Core
 */
public class ActionRegistry {
    private final Map<String, BaseAction> actions;

    public ActionRegistry() {
        this.actions = new HashMap<>();
    }

    /**
     * Register an action.
     * @param actionName The action name
     * @param action The action implementation
     */
    public void register(String actionName, BaseAction action) {
        actions.put(actionName, action);
    }

    /**
     * Get an action by name.
     * @param actionName The action name
     * @return The action, or null if not found
     */
    public BaseAction get(String actionName) {
        return actions.get(actionName);
    }

    /**
     * Check if an action is registered.
     * @param actionName The action name
     * @return True if registered, false otherwise
     */
    public boolean isRegistered(String actionName) {
        return actions.containsKey(actionName);
    }

    /**
     * Get all registered action names.
     * @return Set of action names
     */
    public java.util.Set<String> getRegisteredActions() {
        return new java.util.HashSet<>(actions.keySet());
    }

    /**
     * Get the number of registered actions.
     * @return The count of actions
     */
    public int size() {
        return actions.size();
    }
}
