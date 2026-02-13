package com.moxred.core.execution;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the result of an action execution.
 */
public class ActionResult {
    private final boolean success;
    private final String message;
    private final Map<String, Object> data;

    public ActionResult(boolean success, String message, Map<String, Object> data) {
        this.success = success;
        this.message = message;
        this.data = data != null ? data : new HashMap<>();
    }

    public static ActionResult success(String message) {
        return new ActionResult(true, message, new HashMap<>());
    }

    public static ActionResult success(String message, Map<String, Object> data) {
        return new ActionResult(true, message, data);
    }

    public static ActionResult failure(String message) {
        return new ActionResult(false, message, new HashMap<>());
    }

    public static ActionResult failure(String message, Map<String, Object> data) {
        return new ActionResult(false, message, data);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ActionResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
