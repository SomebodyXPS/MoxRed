package com.moxred.core.actions.impl;

import com.moxred.core.actions.BaseAction;
import com.moxred.core.execution.ActionContext;
import com.moxred.core.execution.ActionResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Removes operator status from a player.
 */
public class DeopPlayerAction implements BaseAction {

    @Override
    public String getName() {
        return "DEOP_PLAYER";
    }

    @Override
    public ActionResult execute(ActionContext context) throws Exception {
        String playerName = context.getString("player");

        if (playerName == null || playerName.isEmpty()) {
            return ActionResult.failure("Player name is required");
        }

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            return ActionResult.failure("Player not found: " + playerName);
        }

        player.setOp(false);
        return ActionResult.success("Player " + playerName + " deopped successfully");
    }
}
