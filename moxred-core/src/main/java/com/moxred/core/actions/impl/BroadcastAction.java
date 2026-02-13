package com.moxred.core.actions.impl;

import com.moxred.core.actions.BaseAction;
import com.moxred.core.execution.ActionContext;
import com.moxred.core.execution.ActionResult;
import org.bukkit.Bukkit;

/**
 * Broadcasts a message to all players on the server.
 */
public class BroadcastAction implements BaseAction {

    @Override
    public String getName() {
        return "BROADCAST";
    }

    @Override
    public ActionResult execute(ActionContext context) throws Exception {
        String message = context.getString("message");

        if (message == null || message.isEmpty()) {
            return ActionResult.failure("Message is required");
        }

        Bukkit.broadcastMessage(message);
        return ActionResult.success("Message broadcasted: " + message);
    }
}
