package com.moxred.core.security;

/**
 * Validates timestamps on packets.
 */
public class TimestampValidator {

    /**
     * Validate a timestamp is within acceptable range.
     * @param timestamp The timestamp to validate (Unix seconds)
     * @param toleranceSeconds The acceptable tolerance in seconds
     * @return True if timestamp is valid, false otherwise
     */
    public static boolean validate(long timestamp, long toleranceSeconds) {
        long currentTime = System.currentTimeMillis() / 1000;
        long difference = Math.abs(currentTime - timestamp);
        return difference <= toleranceSeconds;
    }

    /**
     * Get the current Unix timestamp in seconds.
     * @return Current Unix timestamp
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis() / 1000;
    }
}
