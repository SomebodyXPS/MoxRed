package com.moxred.core.telemetry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Telemetry collector for monitoring plugin performance and usage.
 */
public class TelemetryCollector {
    private final AtomicLong totalPacketsReceived;
    private final AtomicLong totalActionsExecuted;
    private final AtomicLong totalSecurityFailures;
    private final Map<String, AtomicLong> actionExecutionCounts;
    private final long startTime;

    public TelemetryCollector() {
        this.totalPacketsReceived = new AtomicLong(0);
        this.totalActionsExecuted = new AtomicLong(0);
        this.totalSecurityFailures = new AtomicLong(0);
        this.actionExecutionCounts = new HashMap<>();
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Record a packet received.
     */
    public void recordPacketReceived() {
        totalPacketsReceived.incrementAndGet();
    }

    /**
     * Record an action execution.
     * @param actionName The name of the action
     */
    public void recordActionExecuted(String actionName) {
        totalActionsExecuted.incrementAndGet();
        actionExecutionCounts.computeIfAbsent(actionName, k -> new AtomicLong(0))
                .incrementAndGet();
    }

    /**
     * Record a security failure.
     */
    public void recordSecurityFailure() {
        totalSecurityFailures.incrementAndGet();
    }

    /**
     * Get telemetry statistics.
     * @return Map of telemetry data
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("uptime_ms", System.currentTimeMillis() - startTime);
        stats.put("packets_received", totalPacketsReceived.get());
        stats.put("actions_executed", totalActionsExecuted.get());
        stats.put("security_failures", totalSecurityFailures.get());
        stats.put("action_counts", new HashMap<>(actionExecutionCounts.entrySet().stream()
                .collect(HashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue().get()),
                        HashMap::putAll)));
        return stats;
    }
}
