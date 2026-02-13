package com.moxred.bot.conversation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

/**
 * Manages conversation history and context per user
 * Maintains multi-turn conversation state for natural dialogue
 */
public class ConversationManager {
    private final Map<String, UserConversation> conversations;
    private final int maxHistoryPerUser;
    private final long conversationTimeoutMs;

    public ConversationManager(int maxHistoryPerUser, long conversationTimeoutMs) {
        this.conversations = new ConcurrentHashMap<>();
        this.maxHistoryPerUser = maxHistoryPerUser;
        this.conversationTimeoutMs = conversationTimeoutMs;
    }

    public ConversationManager() {
        this(50, 3600000); // 50 messages, 1 hour timeout
    }

    /**
     * Get or create conversation for a user
     */
    public UserConversation getConversation(String userId) {
        return conversations.computeIfAbsent(userId, k -> new UserConversation(userId));
    }

    /**
     * Add a message to user's conversation
     */
    public void addMessage(String userId, ConversationMessage message) {
        UserConversation conv = getConversation(userId);
        conv.addMessage(message);
        
        // Trim history if too long
        while (conv.getMessages().size() > maxHistoryPerUser) {
            conv.getMessages().remove(0);
        }
    }

    /**
     * Add user message
     */
    public void addUserMessage(String userId, String content) {
        addMessage(userId, new ConversationMessage(
            ConversationMessage.Role.USER,
            content,
            LocalDateTime.now()
        ));
    }

    /**
     * Add AI message
     */
    public void addAIMessage(String userId, String content) {
        addMessage(userId, new ConversationMessage(
            ConversationMessage.Role.ASSISTANT,
            content,
            LocalDateTime.now()
        ));
    }

    /**
     * Get conversation history for a user as text
     */
    public String getConversationHistoryAsText(String userId) {
        UserConversation conv = getConversation(userId);
        StringBuilder sb = new StringBuilder();
        
        for (ConversationMessage msg : conv.getMessages()) {
            String role = msg.getRole() == ConversationMessage.Role.USER ? "User" : "Assistant";
            sb.append(role).append(": ").append(msg.getContent()).append("\n");
        }
        
        return sb.toString();
    }

    /**
     * Clear conversation for a user
     */
    public void clearConversation(String userId) {
        conversations.remove(userId);
    }

    /**
     * Get recent messages for context
     */
    public List<ConversationMessage> getRecentMessages(String userId, int count) {
        UserConversation conv = getConversation(userId);
        List<ConversationMessage> messages = conv.getMessages();
        int startIndex = Math.max(0, messages.size() - count);
        return new ArrayList<>(messages.subList(startIndex, messages.size()));
    }

    /**
     * Clear expired conversations
     */
    public void clearExpiredConversations() {
        long now = System.currentTimeMillis();
        conversations.entrySet().removeIf(entry -> 
            (now - entry.getValue().getLastActivityMs()) > conversationTimeoutMs
        );
    }

    /**
     * Represents a single conversation for a user
     */
    public static class UserConversation {
        private final String userId;
        private final List<ConversationMessage> messages;
        private long lastActivityMs;

        public UserConversation(String userId) {
            this.userId = userId;
            this.messages = Collections.synchronizedList(new ArrayList<>());
            this.lastActivityMs = System.currentTimeMillis();
        }

        public void addMessage(ConversationMessage message) {
            messages.add(message);
            this.lastActivityMs = System.currentTimeMillis();
        }

        public String getUserId() {
            return userId;
        }

        public List<ConversationMessage> getMessages() {
            return messages;
        }

        public long getLastActivityMs() {
            return lastActivityMs;
        }

        public int getMessageCount() {
            return messages.size();
        }
    }

    /**
     * Represents a single message in conversation
     */
    public static class ConversationMessage {
        public enum Role {
            USER,
            ASSISTANT,
            SYSTEM
        }

        private final Role role;
        private final String content;
        private final LocalDateTime timestamp;

        public ConversationMessage(Role role, String content, LocalDateTime timestamp) {
            this.role = role;
            this.content = content;
            this.timestamp = timestamp;
        }

        public Role getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return role + ": " + content;
        }
    }
}
