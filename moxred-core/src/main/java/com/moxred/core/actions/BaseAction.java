package com.moxred.core.actions;

import com.moxred.core.execution.ActionContext;
import com.moxred.core.execution.ActionResult;

/**
 * Base interface for all actions.
 */
public interface BaseAction {
    /**
     * Get the action name.
     * @return The action name
     */
    String getName();

    /**
     * Execute the action.
     * @param context The execution context
     * @return The result of execution
     * @throws Exception If execution fails
     */
    ActionResult execute(ActionContext context) throws Exception;
}
