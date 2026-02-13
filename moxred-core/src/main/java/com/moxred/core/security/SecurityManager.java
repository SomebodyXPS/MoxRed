package com.moxred.core.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.moxred.core.audit.AuditLogger;
import com.moxred.core.config.CoreConfig;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Manages security validation for incoming packets.
 */
public class SecurityManager {
    private final CoreConfig config;
    private final SignatureValidator signatureValidator;
    private final TimestampValidator timestampValidator;
    private final NonceStore nonceStore;
    private final AuditLogger auditLogger;

    public SecurityManager(CoreConfig config, AuditLogger auditLogger) {
        this.config = config;
        this.auditLogger = auditLogger;
        this.nonceStore = new NonceStore(config.getNonceCacheSize());
        this.signatureValidator = new SignatureValidator();
        this.timestampValidator = new TimestampValidator();
    }

    /**
     * Validate an incoming packet.
     * @param packet The JSON packet to validate
     * @param rawPayload The raw JSON payload (for signature verification)
     * @return True if packet is valid, false otherwise
     */
    public boolean validatePacket(JsonNode packet, String rawPayload) {
        // Check protocol version
        if (!packet.has("protocolVersion")) {
            auditLogger.log("SECURITY", "Missing protocol version");
            return false;
        }

        int protocolVersion = packet.get("protocolVersion").asInt();
        if (protocolVersion != config.getProtocolVersion()) {
            auditLogger.log("SECURITY", "Invalid protocol version: " + protocolVersion);
            return false;
        }

        // Check signature
        if (!packet.has("signature")) {
            auditLogger.log("SECURITY", "Missing signature");
            return false;
        }

        String signature = packet.get("signature").asText();
        if (!SignatureValidator.validate(rawPayload, signature, config.getSharedSecret())) {
            auditLogger.log("SECURITY", "Invalid signature");
            return false;
        }

        // Check timestamp
        if (packet.has("timestamp")) {
            long timestamp = packet.get("timestamp").asLong();
            if (!TimestampValidator.validate(timestamp, config.getTimestampToleranceSeconds())) {
                auditLogger.log("SECURITY", "Timestamp out of tolerance");
                return false;
            }
        }

        // Check nonce
        if (packet.has("nonce")) {
            String nonce = packet.get("nonce").asText();
            if (!nonceStore.checkAndAdd(nonce)) {
                auditLogger.log("SECURITY", "Replay attack detected: " + nonce);
                return false;
            }
        }

        return true;
    }

    public NonceStore getNonceStore() {
        return nonceStore;
    }
}
