package com.moxred.core.security;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores and validates nonces to prevent replay attacks.
 */
public class NonceStore {
    private final int maxSize;
    private final Map<String, Long> nonces;

    public NonceStore(int maxSize) {
        this.maxSize = maxSize;
        this.nonces = new ConcurrentHashMap<>();
    }

    /**
     * Check if a nonce has been used before.
     * @param nonce The nonce to check
     * @return True if the nonce is valid (not used), false if it's a replay
     */
    public synchronized boolean checkAndAdd(String nonce) {
        if (nonce == null || nonce.isEmpty()) {
            return false;
        }

        if (nonces.containsKey(nonce)) {
            return false; // Nonce already used
        }

        // If we've reached max size, clear old entries
        if (nonces.size() >= maxSize) {
            clearOldest();
        }

        nonces.put(nonce, System.currentTimeMillis());
        return true;
    }

    /**
     * Clear the oldest nonces to make room for new ones.
     */
    private void clearOldest() {
        int toRemove = maxSize / 10; // Remove 10% of entries
        nonces.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(toRemove)
                .forEach(entry -> nonces.remove(entry.getKey()));
    }

    /**
     * Clear all nonces.
     */
    public synchronized void clear() {
        nonces.clear();
    }

    /**
     * Get current size of nonce store.
     * @return The number of stored nonces
     */
    public int size() {
        return nonces.size();
    }
}
