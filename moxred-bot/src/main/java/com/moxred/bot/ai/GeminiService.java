package com.moxred.bot.ai;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/**
 * Integration with Google Gemini 2.5 Flash for natural language processing
 * Converts user messages into structured action commands via REST API
 */
public class GeminiService {
    private final String apiKey;
    private final ObjectMapper mapper;
    private final boolean verboseLogging;
    private final HttpClient httpClient;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private static final String SYSTEM_PROMPT = """
            You are a Minecraft server command interpreter. Convert natural language requests into JSON action commands.
            
            Available actions:
            - BROADCAST: {action: "BROADCAST", data: {message: string}}
            - OP_PLAYER: {action: "OP_PLAYER", data: {playerName: string}}
            - DEOP_PLAYER: {action: "DEOP_PLAYER", data: {playerName: string}}
            - GIVE_ITEM: {action: "GIVE_ITEM", data: {playerName: string, item: string, quantity: number, enchantments: object}}
            - GIVE_RANK: {action: "GIVE_RANK", data: {playerName: string, rank: string}}
            - GET_TPS: {action: "GET_TPS", data: {}}
            
            Examples:
            - User: "give Steve op" → {"action": "OP_PLAYER", "data": {"playerName": "Steve"}}
            - User: "give Alex diamond sword with sharpness 5" → {"action": "GIVE_ITEM", "data": {"playerName": "Alex", "item": "diamond_sword", "quantity": 1, "enchantments": {"sharpness": 5}}}
            - User: "broadcast hello world" → {"action": "BROADCAST", "data": {"message": "hello world"}}
            - User: "give Mike admin rank" → {"action": "GIVE_RANK", "data": {"playerName": "Mike", "rank": "admin"}}
            - User: "check server status" → {"action": "GET_TPS", "data": {}}
            
            Rules:
            1. Always return valid JSON
            2. Extract player names exactly as stated
            3. For items, use Minecraft item IDs (lowercase, underscores)
            4. For enchantments, use lowercase names and numeric levels
            5. Always wrap response in {"status": "success"} or {"status": "error", "message": "reason"}
            6. Never include markdown code blocks, just raw JSON
            7. If uncertain about the request, return error status
            
            Respond ONLY with JSON, no other text.
            """;

    public GeminiService(String apiKey, boolean verboseLogging) {
        this.apiKey = apiKey;
        this.verboseLogging = verboseLogging;
        this.mapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Parse natural language input into structured action data
     */
    public ParsedCommand parseCommand(String userInput) throws Exception {
        if (userInput == null || userInput.trim().isEmpty()) {
            return new ParsedCommand(null, "Empty input");
        }

        try {
            if (verboseLogging) {
                log("Parsing: " + userInput);
            }

            // Build request to Gemini API
            Map<String, Object> requestBody = buildGeminiRequest(userInput);
            String jsonRequest = mapper.writeValueAsString(requestBody);

            if (verboseLogging) {
                log("Sending to Gemini: " + jsonRequest);
            }

            // Call Gemini API
            String responseText = callGeminiAPI(jsonRequest);

            if (verboseLogging) {
                log("Gemini response: " + responseText);
            }

            // Remove markdown code blocks if present
            String cleanJson = responseText.replaceAll("```json?\\n?", "")
                                          .replaceAll("\\n?```", "")
                                          .trim();

            // Parse JSON response
            Map<String, Object> parsedJson = mapper.readValue(cleanJson, Map.class);
            String status = (String) parsedJson.get("status");

            if ("error".equals(status)) {
                String errorMessage = (String) parsedJson.get("message");
                return new ParsedCommand(null, "Gemini error: " + errorMessage);
            }

            if ("success".equals(status)) {
                String action = (String) parsedJson.get("action");
                Map<String, Object> data = (Map<String, Object>) parsedJson.get("data");

                if (action == null) {
                    return new ParsedCommand(null, "No action parsed");
                }

                return new ParsedCommand(
                    new CommandIntent(action, data),
                    null
                );
            }

            // If no status field, assume the whole thing is the command
            String action = (String) parsedJson.get("action");
            if (action != null) {
                Map<String, Object> data = (Map<String, Object>) parsedJson.get("data");
                return new ParsedCommand(
                    new CommandIntent(action, data != null ? data : new HashMap<>()),
                    null
                );
            }

            return new ParsedCommand(null, "Could not parse command structure");

        } catch (Exception e) {
            logError("Failed to call Gemini API", e);
            return new ParsedCommand(null, "Failed to parse command: " + e.getMessage());
        }
    }

    /**
     * Build request payload for Gemini API
     */
    private Map<String, Object> buildGeminiRequest(String userInput) {
        Map<String, Object> request = new LinkedHashMap<>();
        
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new LinkedHashMap<>();
        
        List<Map<String, Object>> parts = new ArrayList<>();
        
        // Add system instruction as first part
        Map<String, Object> systemPart = new LinkedHashMap<>();
        systemPart.put("text", SYSTEM_PROMPT);
        parts.add(systemPart);
        
        // Add user input
        Map<String, Object> userPart = new LinkedHashMap<>();
        userPart.put("text", "User: " + userInput);
        parts.add(userPart);
        
        content.put("parts", parts);
        contents.add(content);
        
        request.put("contents", contents);
        
        // Add safety settings (allow all)
        // Note: Only include categories supported by Gemini 2.5 Flash API
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
     * Call Gemini API via HTTP
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
            throw new RuntimeException("Gemini API error: " + response.statusCode() + " - " + errorBody);
        }

        // Extract text from Gemini response
        Map<String, Object> responseJson = mapper.readValue(response.body(), Map.class);
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseJson.get("candidates");
        
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("No candidates in Gemini response");
        }

        Map<String, Object> candidate = candidates.get(0);
        Map<String, Object> content = (Map<String, Object>) candidate.get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

        if (parts == null || parts.isEmpty()) {
            throw new RuntimeException("No text parts in Gemini response");
        }

        return (String) parts.get(0).get("text");
    }

    /**
     * Log message
     */
    private void log(String message) {
        System.out.println("[GeminiService] " + message);
    }

    /**
     * Log error
     */
    private void logError(String context, Exception e) {
        System.err.println("[GeminiService] " + context + (e != null ? ": " + e.getMessage() : ""));
        if (verboseLogging && e != null) {
            e.printStackTrace();
        }
    }

    /**
     * Result of parsing a natural language command
     */
    public static class ParsedCommand {
        public final CommandIntent intent;
        public final String error;

        public ParsedCommand(CommandIntent intent, String error) {
            this.intent = intent;
            this.error = error;
        }

        public boolean isSuccess() {
            return error == null && intent != null;
        }

        public String getErrorMessage() {
            return error != null ? error : "Unknown error";
        }
    }

    /**
     * Structured command intent extracted from natural language
     */
    public static class CommandIntent {
        public final String action;
        public final Map<String, Object> data;

        public CommandIntent(String action, Map<String, Object> data) {
            this.action = action;
            this.data = data != null ? data : new HashMap<>();
        }

        public String getAction() {
            return action;
        }

        public Map<String, Object> getData() {
            return data;
        }
    }
}

