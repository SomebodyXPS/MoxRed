package com.moxred.core;

import com.moxred.core.actions.impl.*;
import com.moxred.core.audit.AuditLogger;
import com.moxred.core.config.CoreConfig;
import com.moxred.core.execution.ActionExecutor;
import com.moxred.core.execution.ActionRegistry;
import com.moxred.core.execution.WorkflowExecutor;
import com.moxred.core.telemetry.TelemetryCollector;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * MoxRed-Core: Deterministic Execution Engine
 * Secure, isolated execution layer for Minecraft server operations
 */
public class MoxRedCore extends JavaPlugin {

    private CoreConfig coreConfig;
    private AuditLogger auditLogger;
    private TelemetryCollector telemetry;
    private ActionRegistry actionRegistry;
    private ActionExecutor actionExecutor;
    private WorkflowExecutor workflowExecutor;

    @Override
    public void onLoad() {
        getLogger().info("MoxRed Core loading...");
    }

    @Override
    public void onEnable() {
        getLogger().info("MoxRed Core enabling...");

        // Initialize configuration
        coreConfig = new CoreConfig(this);
        getLogger().info("Configuration loaded");

        // Initialize audit logger
        auditLogger = new AuditLogger(getDataFolder());
        auditLogger.log("STARTUP", "MoxRed Core starting up");

        // Initialize telemetry
        telemetry = new TelemetryCollector();

        // Initialize action registry and register actions
        actionRegistry = new ActionRegistry();
        registerActions();
        getLogger().info("Action registry initialized with " + actionRegistry.size() + " actions");

        // Initialize execution engine
        actionExecutor = new ActionExecutor(actionRegistry, auditLogger, telemetry);
        workflowExecutor = new WorkflowExecutor(actionRegistry);
        getLogger().info("Execution engine initialized");

        getLogger().info("MoxRed Core enabled successfully!");
        getLogger().info("Waiting for local bot plugin to connect...");
        auditLogger.log("STARTUP", "MoxRed Core enabled successfully!");
    }

    @Override
    public void onDisable() {
        auditLogger.log("SHUTDOWN", "MoxRed Core shutting down");
        getLogger().info("MoxRed Core disabling...");
        getLogger().info("MoxRed Core disabled");
    }

    /**
     * Register all available actions.
     */
    private void registerActions() {
        actionRegistry.register("BROADCAST", new BroadcastAction());
        actionRegistry.register("OP_PLAYER", new OpPlayerAction());
        actionRegistry.register("DEOP_PLAYER", new DeopPlayerAction());
        actionRegistry.register("GET_TPS", new GetTPSAction());
        actionRegistry.register("GIVE_ITEM", new GiveItemAction());
        actionRegistry.register("GIVE_RANK", new GiveRankAction());

        getLogger().info("Registered " + actionRegistry.size() + " actions");
    }

    // Getters for accessing components
    public CoreConfig getCoreConfig() {
        return coreConfig;
    }

    public AuditLogger getAuditLogger() {
        return auditLogger;
    }

    public TelemetryCollector getTelemetry() {
        return telemetry;
    }

    public ActionRegistry getActionRegistry() {
        return actionRegistry;
    }

    public ActionExecutor getActionExecutor() {
        return actionExecutor;
    }

    public WorkflowExecutor getWorkflowExecutor() {
        return workflowExecutor;
    }
}
