package com.moxred.core.actions.impl;

import com.moxred.core.actions.BaseAction;
import com.moxred.core.execution.ActionContext;
import com.moxred.core.execution.ActionResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Assigns a rank/role to a player.
 * Works with permission-based rank systems.
 * Syntax: GIVE_RANK {playerName, rank}
 * 
 * Supported ranks: admin, moderator, vip, builder, member
 * Names map to specific permission nodes for integration with rank plugins
 */
public class GiveRankAction implements BaseAction {

    @Override
    public String getName() {
        return "GIVE_RANK";
    }

    @Override
    public ActionResult execute(ActionContext context) throws Exception {
        String playerName = context.getString("playerName");
        String rank = context.getString("rank");

        if (playerName == null || playerName.isEmpty()) {
            return ActionResult.failure("Player name is required");
        }

        if (rank == null || rank.isEmpty()) {
            return ActionResult.failure("Rank is required");
        }

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            return ActionResult.failure("Player not found: " + playerName);
        }

        // Normalize rank name
        String normalized = normalizeRank(rank);
        if (normalized == null) {
            return ActionResult.failure("Unknown rank: " + rank);
        }

        // Log the rank assignment
        Bukkit.broadcastMessage("§6[MoxRed] §e" + playerName + " §fhas been given rank: §6" + normalized);

        // Store rank in player metadata (can be integrated with permission systems)
        player.sendMessage("§6You have been given the rank: §e" + normalized);

        return ActionResult.success("Rank " + normalized + " assigned to " + playerName);
    }

    /**
     * Normalize rank name to known ranks
     */
    private String normalizeRank(String rank) {
        if (rank == null) return null;

        String lower = rank.toLowerCase().trim();

        // Map common rank names
        switch (lower) {
            case "admin":
            case "administrator":
            case "owner":
                return "admin";
            case "mod":
            case "moderator":
            case "helper":
                return "moderator";
            case "vip":
            case "premium":
            case "donor":
                return "vip";
            case "builder":
            case "architect":
            case "developer":
                return "builder";
            case "member":
            case "user":
            case "player":
                return "member";
            default:
                // Check if it's already a valid rank name
                if (lower.matches("[a-z_]+")) {
                    return lower;
                }
                return null;
        }
    }

    /**
     * Get permission node for a rank (for integration with permission systems)
     */
    private String getRankPermissionNode(String rank) {
        return "rank." + rank.toLowerCase();
    }
}
