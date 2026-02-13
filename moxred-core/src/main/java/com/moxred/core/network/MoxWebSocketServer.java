package com.moxred.core.network;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxred.core.audit.AuditLogger;
import com.moxred.core.config.CoreConfig;
import com.moxred.core.execution.ActionExecutor;
import com.moxred.core.execution.ActionResult;
import com.moxred.core.execution.ExecutionMode;
import com.moxred.core.execution.WorkflowExecutor;
import com.moxred.core.security.SecurityManager;
import com.moxred.core.telemetry.TelemetryCollector;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebSocket server for receiving and processing packets.
 */
public class MoxWebSocketServer extends WebSocketServer {
    private final CoreConfig config;
    private final SecurityManager securityManager;
    private final ActionExecutor actionExecutor;
    private final WorkflowExecutor workflowExecutor;
    private final ResponseSender responseSender;
    private final AuditLogger auditLogger;
    private final TelemetryCollector telemetry;
    private final ObjectMapper objectMapper;
    private final JavaPlugin plugin;

    public MoxWebSocketServer(InetSocketAddress address, JavaPlugin plugin, CoreConfig config,
                             SecurityManager securityManager, ActionExecutor actionExecutor,
                             WorkflowExecutor workflowExecutor, AuditLogger auditLogger,
                             TelemetryCollector telemetry) {
        super(address);
        this.plugin = plugin;
        this.config = config;
        this.securityManager = securityManager;
        this.actionExecutor = actionExecutor;
        this.workflowExecutor = workflowExecutor;
        this.auditLogger = auditLogger;
        this.telemetry = telemetry;
        this.responseSender = new ResponseSender();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        auditLogger.log("NETWORK", "Client connected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        auditLogger.log("NETWORK", "Client disconnected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        telemetry.recordPacketReceived();

        try {
            // Parse JSON
            JsonNode packet = objectMapper.readTree(message);

            // Validate packet security
            if (!securityManager.validatePacket(packet, message)) {
                telemetry.recordSecurityFailure();
                responseSender.sendResponse(conn, false, "Security validation failed");
                return;
            }

            // Get execution mode
            String modeString = packet.has("mode") ? packet.get("mode").asText() : "SAFE_ACTION";
            ExecutionMode mode;
            try {
                mode = ExecutionMode.valueOf(modeString);
            } catch (IllegalArgumentException e) {
                responseSender.sendResponse(conn, false, "Invalid execution mode: " + modeString);
                return;
            }

            // Execute based on mode
            if (mode == ExecutionMode.SAFE_ACTION) {
                executeSafeAction(packet, conn);
            } else if (mode == ExecutionMode.WORKFLOW) {
                if (!config.isEnableWorkflowMode()) {
                    responseSender.sendResponse(conn, false, "Workflow mode is disabled");
                    return;
                }
                executeWorkflow(packet, conn);
            } else {
                responseSender.sendResponse(conn, false, "Unknown execution mode");
            }

        } catch (Exception e) {
            auditLogger.log("ERROR", "Failed to process message: " + e.getMessage());
            responseSender.sendResponse(conn, false, "Processing error: " + e.getMessage());
        }
    }

    /**
     * Execute a single safe action.
     */
    private void executeSafeAction(JsonNode packet, WebSocket conn) {
        String actionName = packet.has("action") ? packet.get("action").asText() : null;

        if (actionName == null || actionName.isEmpty()) {
            responseSender.sendResponse(conn, false, "Action name is required");
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> data = objectMapper.convertValue(
                packet.has("data") ? packet.get("data") : new HashMap<>(),
                Map.class);

        ActionResult result = actionExecutor.execute(actionName, data, plugin);
        responseSender.sendActionResponse(conn, result);
    }

    /**
     * Execute a workflow (sequence of actions).
     */
    private void executeWorkflow(JsonNode packet, WebSocket conn) {
        if (!packet.has("steps")) {
            responseSender.sendResponse(conn, false, "Workflow steps are required");
            return;
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> steps = objectMapper.convertValue(
                packet.get("steps"),
                List.class);

        List<ActionResult> results = workflowExecutor.executeSteps(steps, plugin);
        responseSender.sendWorkflowResponse(conn, results);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        auditLogger.log("ERROR", "WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        auditLogger.log("NETWORK", "MoxWebSocket server started on " + getAddress());
    }
}
