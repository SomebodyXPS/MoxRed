package com.moxred.core.execution;

/**
 * Execution modes for MoxRed Core.
 */
public enum ExecutionMode {
    /**
     * Execute a single validated action.
     */
    SAFE_ACTION,

    /**
     * Execute a sequence of safe actions.
     */
    WORKFLOW
}
