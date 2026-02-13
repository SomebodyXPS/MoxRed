package com.moxred.core.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Validates HMAC-SHA256 signatures on packets.
 */
public class SignatureValidator {

    /**
     * Validate a signature against a payload.
     * @param payload The payload to verify
     * @param signature The signature to verify against
     * @param secret The shared secret
     * @return True if signature is valid, false otherwise
     */
    public static boolean validate(String payload, String signature, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(payload.getBytes());
            String computed = Base64.getEncoder().encodeToString(hash);
            return MessageDigest.isEqual(computed.getBytes(), signature.getBytes());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Compute a signature for a payload.
     * @param payload The payload to sign
     * @param secret The shared secret
     * @return The computed signature
     */
    public static String compute(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(payload.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute signature", e);
        }
    }
}
