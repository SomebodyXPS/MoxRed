package com.moxred.core.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxred.core.execution.ActionResult;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sends structured responses to clients.
 */
public class ResponseSender {
    private final ObjectMapper objectMapper;

    public ResponseSender() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Send a simple response.
     * @param conn The WebSocket connection
     * @param success Whether the operation was successful
     * @param message The response message
     */
    public void sendResponse(WebSocket conn, boolean success, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis() / 1000);

        try {
            String json = objectMapper.writeValueAsString(response);
            conn.send(json);
        } catch (Exception e) {
            System.err.println("Failed to send response: " + e.getMessage());
        }
    }

    /**
     * Send an action response.
     * @param conn The WebSocket connection
     * @param result The action result
     */
    public void sendActionResponse(WebSocket conn, ActionResult result) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        response.put("data", result.getData());
        response.put("timestamp", System.currentTimeMillis() / 1000);

        try {
            String json = objectMapper.writeValueAsString(response);
            conn.send(json);
        } catch (Exception e) {
            System.err.println("Failed to send action response: " + e.getMessage());
        }
    }

    /**
     * Send a workflow response.
     * @param conn The WebSocket connection
     * @param results The list of action results
     */
    public void sendWorkflowResponse(WebSocket conn, List<ActionResult> results) {
        Map<String, Object> response = new HashMap<>();

        boolean allSuccess = results.stream().allMatch(ActionResult::isSuccess);
        response.put("success", allSuccess);
        response.put("steps_count", results.size());

        List<Map<String, Object>> stepsData = new ArrayList<>();
        for (ActionResult result : results) {
            Map<String, Object> stepData = new HashMap<>();
            stepData.put("success", result.isSuccess());
            stepData.put("message", result.getMessage());
            stepData.put("data", result.getData());
            stepsData.add(stepData);
        }
        response.put("steps", stepsData);
        response.put("timestamp", System.currentTimeMillis() / 1000);

        try {
            String json = objectMapper.writeValueAsString(response);
            conn.send(json);
        } catch (Exception e) {
            System.err.println("Failed to send workflow response: " + e.getMessage());
        }
    }
}
