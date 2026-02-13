package com.moxred.core.audit;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Audit logger for recording all security and action events.
 */
public class AuditLogger {
    private final File auditLogFile;
    private final DateTimeFormatter formatter;

    public AuditLogger(File pluginDataFolder) {
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.auditLogFile = new File(pluginDataFolder, "audit.log");

        if (!pluginDataFolder.exists()) {
            pluginDataFolder.mkdirs();
        }

        if (!auditLogFile.exists()) {
            try {
                auditLogFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Failed to create audit log file: " + e.getMessage());
            }
        }
    }

    /**
     * Log an event to the audit log.
     * @param category The category of the event
     * @param message The event message
     */
    public void log(String category, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[%s] %s: %s\n", timestamp, category, message);

        try (FileWriter writer = new FileWriter(auditLogFile, true)) {
            writer.write(logEntry);
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to audit log: " + e.getMessage());
        }
    }

    /**
     * Log an action execution.
     * @param action The action name
     * @param success Whether it succeeded
     * @param details Additional details
     */
    public void logAction(String action, boolean success, String details) {
        String status = success ? "SUCCESS" : "FAILURE";
        log("ACTION_" + status, action + " - " + details);
    }
}
