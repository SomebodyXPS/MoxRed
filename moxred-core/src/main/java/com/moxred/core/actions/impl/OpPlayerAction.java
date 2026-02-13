package com.moxred.core.actions.impl;

import com.moxred.core.actions.BaseAction;
import com.moxred.core.execution.ActionContext;
import com.moxred.core.execution.ActionResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Grants operator status to a player.
 */
public class OpPlayerAction implements BaseAction {

    @Override
    public String getName() {
        return "OP_PLAYER";
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

        player.setOp(true);
        return ActionResult.success("Player " + playerName + " opped successfully");
    }
}
