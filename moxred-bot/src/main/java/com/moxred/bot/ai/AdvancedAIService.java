package com.moxred.bot.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxred.bot.data.ServerContextProvider;
import com.moxred.bot.conversation.ConversationManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/**
 * Advanced AI service with live server context and conversation awareness
 * Integrates Gemini 2.5 Flash with real-time server data and conversation history
 */
public class AdvancedAIService {
    private final String apiKey;
    private final ObjectMapper mapper;
    private final boolean verboseLogging;
    private final HttpClient httpClient;
    private final ServerContextProvider serverContext;
    private final ConversationManager conversationManager;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    public AdvancedAIService(String apiKey, ServerContextProvider serverContext, 
                            ConversationManager conversationManager, boolean verboseLogging) {
        this.apiKey = apiKey;
        this.verboseLogging = verboseLogging;
        this.mapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
        this.serverContext = serverContext;
        this.conversationManager = conversationManager;
    }

    /**
     * Process natural language command with full context
     * Returns action recommendations and responses
     */
    public AIResponse processCommand(String userId, String userMessage) throws Exception {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return new AIResponse(false, "Empty message", null);
        }

        try {
            // Add user message to conversation
            conversationManager.addUserMessage(userId, userMessage);

            // Build system prompt with live context
            String systemPrompt = buildSystemPrompt();
            
            // Get conversation history
            String conversationHistory = conversationManager.getConversationHistoryAsText(userId);
            
            // Build enhanced request
            Map<String, Object> requestBody = buildGeminiRequest(
                systemPrompt,
                conversationHistory,
                userMessage
            );
            
            String jsonRequest = mapper.writeValueAsString(requestBody);

            if (verboseLogging) {
                log("Processing: " + userMessage);
                log("System prompt: " + systemPrompt.substring(0, Math.min(200, systemPrompt.length())) + "...");
            }

            // Call Gemini API
            String responseText = callGeminiAPI(jsonRequest);

            if (verboseLogging) {
                log("AI Response: " + responseText.substring(0, Math.min(200, responseText.length())) + "...");
            }

            // Parse response
            Map<String, Object> parsedResponse = parseAIResponse(responseText);
            
            // Add AI response to conversation
            String aiMessage = (String) parsedResponse.getOrDefault("message", responseText);
            conversationManager.addAIMessage(userId, aiMessage);

            return new AIResponse(true, aiMessage, parsedResponse);

        } catch (Exception e) {
            logError("Failed to process command", e);
            return new AIResponse(false, "Error: " + e.getMessage(), null);
        }
    }

    /**
     * Build system prompt with live server data and action capabilities
     */
    private String buildSystemPrompt() {
        String serverState = serverContext.getServerContextAsText();
        
        return """
You are MoxRed, an advanced AI control system for a Minecraft server. You help administrators manage the server through natural conversation.

""" + serverState + """

=== AVAILABLE ACTIONS ===
You can execute the following actions when users request them:

1. BROADCAST - Send a message to all players
   Usage: When user asks to announce, tell, broadcast, or message all players
   Example: "Broadcast maintenance starting in 5 minutes"

2. OP_PLAYER - Grant operator status to a player
   Usage: When user asks to op, promote, or give admin to a player
   Example: "Op the player Steve"

3. DEOP_PLAYER - Revoke operator status from a player
   Usage: When user asks to deop, demote, or remove admin from a player
   Example: "Remove admin from Alex"

4. GIVE_ITEM - Give items to a player with optional properties
   Usage: When user asks to give, provide, or gift items
   Example: "Give Steve a diamond sword with sharpness 5"
   Parameters: playerName (required), item (required), quantity (optional), enchantments (optional)

5. GIVE_RANK - Assign a rank/role to a player
   Usage: When user asks to rank, promote, or give role
   Example: "Make Mike a builder"
   Ranks: admin, moderator, vip, builder, member

6. GET_TPS - Check server status
   Usage: When user asks for status, performance, health, or TPS

=== RESPONSE FORMAT ===
When you understand a user's request:
1. First, acknowledge what you understand
2. If action is needed, recommend the action in JSON format
3. End with a friendly response

For actions, respond with JSON following this pattern:
{
  "understanding": "Brief explanation of what user asked",
  "action": "ACTION_NAME",
  "parameters": {
    "param1": "value1",
    "param2": "value2"
  },
  "confidence": 0.95,
  "message": "What to tell the user"
}

If user request is just asking for information or chatting:
{
  "understanding": "Explanation",
  "action": null,
  "parameters": null,
  "confidence": 1.0,
  "message": "Your response"
}

=== RULES ===
1. ONLY execute actions explicitly requested by the user
2. Always confirm actions before showing recommendations
3. Be helpful and explain what you're doing
4. Use the live server data to provide context
5. Handle edge cases gracefully (e.g., player not found → suggest online players)
6. Never execute dangerous actions without explicit confirmation
7. Keep responses concise and friendly
8. If you're unsure, ask clarifying questions

IMPORTANT: Always respond with valid JSON wrapped as a single object, no markdown formatting.
""";
    }

    /**
     * Build Gemini API request with conversation context
     */
    private Map<String, Object> buildGeminiRequest(String systemPrompt, String conversationHistory, String userMessage) {
        Map<String, Object> request = new LinkedHashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new LinkedHashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();

        // System prompt
        Map<String, Object> systemPart = new LinkedHashMap<>();
        systemPart.put("text", systemPrompt);
        parts.add(systemPart);

        // Conversation history (if any)
        if (!conversationHistory.trim().isEmpty()) {
            Map<String, Object> historyPart = new LinkedHashMap<>();
            historyPart.put("text", "=== CONVERSATION HISTORY ===\n" + conversationHistory);
            parts.add(historyPart);
        }

        // Current user message
        Map<String, Object> userPart = new LinkedHashMap<>();
        userPart.put("text", "User: " + userMessage);
        parts.add(userPart);

        content.put("parts", parts);
        contents.add(content);
        request.put("contents", contents);

        // Safety settings
        List<Map<String, Object>> safetySettings = new ArrayList<>();
        String[] categories = {
            "HARM_CATEGORY_HATE_SPEECH",
            "HARM_CATEGORY_SEXUALLY_EXPLICIT",
            "HARM_CATEGORY_DANGEROUS_CONTENT",
            "HARM_CATEGORY_HARASSMENT",
            "HARM_CATEGORY_CIVIC_INTEGRITY"
        };

        for (String category : categories) {
            Map<String, Object> setting = new LinkedHashMap<>();
            setting.put("category", category);
            setting.put("threshold", "BLOCK_NONE");
            safetySettings.add(setting);
        }
        request.put("safetySettings", safetySettings);

        return request;
    }

    /**
     * Call Gemini API
     */
    private String callGeminiAPI(String jsonRequest) throws Exception {
        String url = GEMINI_API_URL + "?key=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
            .timeout(java.time.Duration.ofSeconds(30))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            String errorBody = response.body();
            logError("Gemini API error (" + response.statusCode() + "): " + errorBody, null);
            throw new RuntimeException("Gemini API error: " + response.statusCode());
        }

        Map<String, Object> responseJson = mapper.readValue(response.body(), Map.class);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseJson.get("candidates");

        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("No candidates in Gemini response");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

        if (parts == null || parts.isEmpty()) {
            throw new RuntimeException("No text in Gemini response");
        }

        return (String) parts.get(0).get("text");
    }

    /**
     * Parse AI response
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseAIResponse(String responseText) throws Exception {
        String cleanJson = responseText.replaceAll("```json?\\n?", "")
                                      .replaceAll("\\n?```", "")
                                      .trim();

        // Try to parse as JSON
        try {
            return mapper.readValue(cleanJson, Map.class);
        } catch (Exception e) {
            // If JSON parsing fails, wrap the response
            Map<String, Object> fallback = new LinkedHashMap<>();
            fallback.put("message", responseText);
            fallback.put("action", null);
            fallback.put("understanding", "Unable to parse structured response");
            return fallback;
        }
    }

    private void log(String message) {
        System.out.println("[AdvancedAIService] " + message);
    }

    private void logError(String context, Exception e) {
        System.err.println("[AdvancedAIService] " + context + (e != null ? ": " + e.getMessage() : ""));
        if (verboseLogging && e != null) {
            e.printStackTrace();
        }
    }

    /**
     * AI response with action recommendations
     */
    public static class AIResponse {
        public final boolean success;
        public final String message;
        public final Map<String, Object> metadata;

        public AIResponse(boolean success, String message, Map<String, Object> metadata) {
            this.success = success;
            this.message = message;
            this.metadata = metadata != null ? metadata : new HashMap<>();
        }

        public String getAction() {
            return (String) metadata.getOrDefault("action", null);
        }

        @SuppressWarnings("unchecked")
        public Map<String, Object> getParameters() {
            return (Map<String, Object>) metadata.getOrDefault("parameters", new HashMap<>());
        }

        public double getConfidence() {
            Object conf = metadata.get("confidence");
            if (conf instanceof Number) {
                return ((Number) conf).doubleValue();
            }
            return 0.0;
        }
    }
}
