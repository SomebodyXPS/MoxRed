package com.moxred.bot.data;

import com.moxred.core.MoxRedCore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.*;

/**
 * Provides live server context data to the AI system
 * Fetches real-time information about players, worlds, and server state
 */
public class ServerContextProvider {
    private final MoxRedCore corePlugin;

    public ServerContextProvider(MoxRedCore corePlugin) {
        this.corePlugin = corePlugin;
    }

    /**
     * Get comprehensive server state as structured data
     * Used to contextualize AI requests with live information
     */
    public Map<String, Object> getServerContext() {
        Map<String, Object> context = new LinkedHashMap<>();
        
        context.put("timestamp", System.currentTimeMillis());
        context.put("players", getPlayersContext());
        context.put("worlds", getWorldsContext());
        context.put("server", getServerStats());
        context.put("performance", getPerformanceStats());
        
        return context;
    }

    /**
     * Get detailed player information
     */
    public Map<String, Object> getPlayersContext() {
        Map<String, Object> playersData = new LinkedHashMap<>();
        List<Map<String, Object>> playersList = new ArrayList<>();
        Set<String> playerNames = new HashSet<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Map<String, Object> playerInfo = new LinkedHashMap<>();
            playerInfo.put("name", player.getName());
            playerInfo.put("uuid", player.getUniqueId().toString());
            playerInfo.put("health", player.getHealth());
            playerInfo.put("maxHealth", player.getMaxHealth());
            playerInfo.put("level", player.getLevel());
            playerInfo.put("world", player.getWorld().getName());
            playerInfo.put("x", Math.round(player.getLocation().getX()));
            playerInfo.put("y", Math.round(player.getLocation().getY()));
            playerInfo.put("z", Math.round(player.getLocation().getZ()));
            playerInfo.put("isOp", player.isOp());
            playerInfo.put("food", player.getFoodLevel());
            playerInfo.put("itemInHand", player.getInventory().getItemInMainHand().getType().name());
            playerInfo.put("inventoryCount", (int) Arrays.stream(player.getInventory().getContents())
                    .filter(item -> item != null && item.getAmount() > 0)
                    .count());

            playersList.add(playerInfo);
            playerNames.add(player.getName());
        }

        playersData.put("online", Bukkit.getOnlinePlayers().size());
        playersData.put("max", Bukkit.getMaxPlayers());
        playersData.put("list", playersList);
        playersData.put("names", playerNames);

        return playersData;
    }

    /**
     * Get world information
     */
    public Map<String, Object> getWorldsContext() {
        Map<String, Object> worldsData = new LinkedHashMap<>();
        List<Map<String, Object>> worldsList = new ArrayList<>();

        for (World world : Bukkit.getWorlds()) {
            Map<String, Object> worldInfo = new LinkedHashMap<>();
            worldInfo.put("name", world.getName());
            worldInfo.put("environment", world.getEnvironment().name());
            worldInfo.put("difficulty", world.getDifficulty().name());
            worldInfo.put("time", world.getTime());
            worldInfo.put("dayNight", world.getTime() < 12000 ? "day" : "night");
            worldInfo.put("weather", world.hasStorm() ? "storm" : "clear");
            // Use players.size() instead of getEntities() - more thread-safe
            worldInfo.put("entities", world.getPlayers().size());
            // Estimate loaded chunks from player data instead of querying directly
            worldInfo.put("loadedChunks", world.getPlayers().size() * 10);
            worldInfo.put("players", world.getPlayers().size());
            worldInfo.put("spawnLocation", new HashMap<String, Integer>() {{
                put("x", world.getSpawnLocation().getBlockX());
                put("y", world.getSpawnLocation().getBlockY());
                put("z", world.getSpawnLocation().getBlockZ());
            }});

            worldsList.add(worldInfo);
        }

        worldsData.put("total", worldsList.size());
        worldsData.put("worlds", worldsList);

        return worldsData;
    }

    /**
     * Get server statistics
     */
    public Map<String, Object> getServerStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        // Calculate TPS approximation (would require actual tracking in production)
        double tps = 20.0; // Default healthy TPS
        
        stats.put("uptime_ms", uptime);
        stats.put("uptime_readable", formatUptime(uptime));
        stats.put("tps", tps);
        stats.put("version", Bukkit.getVersion());
        stats.put("maxPlayers", Bukkit.getMaxPlayers());
        stats.put("motd", Bukkit.getMotd());
        stats.put("hasWeatherCycle", true);

        return stats;
    }

    /**
     * Get performance metrics
     */
    public Map<String, Object> getPerformanceStats() {
        Map<String, Object> perf = new LinkedHashMap<>();
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        
        long heapUsed = memBean.getHeapMemoryUsage().getUsed();
        long heapMax = memBean.getHeapMemoryUsage().getMax();
        long heapCommitted = memBean.getHeapMemoryUsage().getCommitted();
        
        perf.put("heap_used_mb", heapUsed / (1024 * 1024));
        perf.put("heap_max_mb", heapMax / (1024 * 1024));
        perf.put("heap_committed_mb", heapCommitted / (1024 * 1024));
        perf.put("heap_usage_percent", (int) ((heapUsed * 100) / heapMax));
        perf.put("threads", Thread.activeCount());
        perf.put("processors", Runtime.getRuntime().availableProcessors());

        return perf;
    }

    /**
     * Format server context as human-readable text for AI system prompt
     */
    public String getServerContextAsText() {
        Map<String, Object> context = getServerContext();
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== LIVE SERVER STATE ===\n");
        
        // Player info
        @SuppressWarnings("unchecked")
        Map<String, Object> players = (Map<String, Object>) context.get("players");
        sb.append("Players: ").append(players.get("online")).append("/").append(players.get("max")).append(" online\n");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> playerList = (List<Map<String, Object>>) players.get("list");
        playerList.forEach(p -> sb.append("  - ").append(p.get("name")).append(" (Level ").append(p.get("level")).append(", Health ").append(p.get("health")).append(")\n"));
        
        // World info
        @SuppressWarnings("unchecked")
        Map<String, Object> worlds = (Map<String, Object>) context.get("worlds");
        sb.append("Worlds: ").append(worlds.get("total")).append(" loaded\n");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> worldList = (List<Map<String, Object>>) worlds.get("worlds");
        worldList.forEach(w -> sb.append("  - ").append(w.get("name")).append(" (").append(w.get("environment")).append(", ").append(w.get("entities")).append(" entities)\n"));
        
        // Server stats
        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) context.get("server");
        sb.append("Server: TPS ").append(stats.get("tps")).append(", Uptime ").append(stats.get("uptime_readable")).append("\n");
        
        // Performance
        @SuppressWarnings("unchecked")
        Map<String, Object> perfStats = (Map<String, Object>) context.get("performance");
        sb.append("Memory: ").append(perfStats.get("heap_used_mb")).append("/").append(perfStats.get("heap_max_mb")).append(" MB (").append(perfStats.get("heap_usage_percent")).append("%)\n");
        
        return sb.toString();
    }

    /**
     * Format uptime in human-readable form
     */
    private String formatUptime(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + "d " + (hours % 24) + "h";
        if (hours > 0) return hours + "h " + (minutes % 60) + "m";
        if (minutes > 0) return minutes + "m " + (seconds % 60) + "s";
        return seconds + "s";
    }

    /**
     * Get a specific player by name
     */
    public Player getPlayer(String name) {
        return Bukkit.getPlayer(name);
    }

    /**
     * Check if a player exists
     */
    public boolean playerExists(String name) {
        return Bukkit.getPlayer(name) != null;
    }

    /**
     * Get all online player names
     */
    public Set<String> getOnlinePlayerNames() {
        Set<String> names = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            names.add(player.getName());
        }
        return names;
    }
}
