package com.moxred.core.execution;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Execution context provided to actions.
 */
public class ActionContext {
    private final JavaPlugin plugin;
    private final Map<String, Object> inputData;

    public ActionContext(JavaPlugin plugin, Map<String, Object> inputData) {
        this.plugin = plugin;
        this.inputData = inputData != null ? inputData : new HashMap<>();
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Map<String, Object> getInputData() {
        return inputData;
    }

    public String getString(String key) {
        Object value = inputData.get(key);
        return value != null ? value.toString() : null;
    }

    public Integer getInteger(String key) {
        Object value = inputData.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }

    public Long getLong(String key) {
        Object value = inputData.get(key);
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    public Boolean getBoolean(String key) {
        Object value = inputData.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getMap(String key) {
        Object value = inputData.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }

    public Object get(String key) {
        return inputData.get(key);
    }
}
