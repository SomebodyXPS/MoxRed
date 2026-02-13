package com.moxred.core.actions.impl;

import com.moxred.core.actions.BaseAction;
import com.moxred.core.execution.ActionContext;
import com.moxred.core.execution.ActionResult;
import org.bukkit.Bukkit;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Gets the current server uptime and tick information.
 */
public class GetTPSAction implements BaseAction {

    @Override
    public String getName() {
        return "GET_TPS";
    }

    @Override
    public ActionResult execute(ActionContext context) throws Exception {
        Map<String, Object> data = new HashMap<>();
        
        // Get server uptime in milliseconds
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        data.put("uptime_ms", uptime);
        
        // Get player count
        data.put("players_online", Bukkit.getOnlinePlayers().size());
        data.put("max_players", Bukkit.getMaxPlayers());
        
        // Get world count
        data.put("worlds", Bukkit.getWorlds().size());

        return ActionResult.success("Server info retrieved", data);
    }
}
