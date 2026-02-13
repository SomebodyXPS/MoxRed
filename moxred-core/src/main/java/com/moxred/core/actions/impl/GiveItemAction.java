package com.moxred.core.actions.impl;

import com.moxred.core.actions.BaseAction;
import com.moxred.core.execution.ActionContext;
import com.moxred.core.execution.ActionResult;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;

import java.util.Map;

/**
 * Gives an item to a player with optional enchantments.
 * Syntax: GIVE_ITEM {playerName, item, quantity?, enchantments?}
 */
public class GiveItemAction implements BaseAction {

    @Override
    public String getName() {
        return "GIVE_ITEM";
    }

    @Override
    public ActionResult execute(ActionContext context) throws Exception {
        String playerName = context.getString("playerName");
        String itemName = context.getString("item");
        Object quantityObj = context.get("quantity");
        Map<String, Object> enchantments = context.getMap("enchantments");

        if (playerName == null || playerName.isEmpty()) {
            return ActionResult.failure("Player name is required");
        }

        if (itemName == null || itemName.isEmpty()) {
            return ActionResult.failure("Item name is required");
        }

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            return ActionResult.failure("Player not found: " + playerName);
        }

        // Parse quantity (default 1)
        int quantity = 1;
        if (quantityObj instanceof Number) {
            quantity = ((Number) quantityObj).intValue();
        }
        if (quantity < 1 || quantity > 64) {
            return ActionResult.failure("Quantity must be between 1 and 64");
        }

        // Convert item name to Material
        Material material = parseMaterial(itemName);
        if (material == null) {
            return ActionResult.failure("Unknown item: " + itemName);
        }

        // Create item stack
        ItemStack item = new ItemStack(material, quantity);

        // Apply enchantments if provided
        if (enchantments != null && !enchantments.isEmpty()) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return ActionResult.failure("Item " + itemName + " cannot have enchantments");
            }

            for (Map.Entry<String, Object> entry : enchantments.entrySet()) {
                String enchantName = entry.getKey();
                Object levelObj = entry.getValue();

                if (!(levelObj instanceof Number)) {
                    return ActionResult.failure("Enchantment level must be a number");
                }

                int level = ((Number) levelObj).intValue();
                Enchantment enchant = Enchantment.getByName(enchantName.toUpperCase());

                if (enchant == null) {
                    return ActionResult.failure("Unknown enchantment: " + enchantName);
                }

                if (level < 1 || level > 32767) {
                    return ActionResult.failure("Enchantment level must be between 1 and 32767");
                }

                meta.addEnchant(enchant, level, true);
            }

            item.setItemMeta(meta);
        }

        // Give item to player
        player.getInventory().addItem(item);
        return ActionResult.success("Given " + quantity + " " + itemName + " to " + playerName);
    }

    /**
     * Parse item name string to Bukkit Material enum
     * Handles both legacy names and modern names
     */
    private Material parseMaterial(String name) {
        if (name == null) return null;

        String upper = name.toUpperCase();

        // Try direct match first
        try {
            return Material.valueOf(upper);
        } catch (IllegalArgumentException e) {
            // Try with common replacements
            String normalized = upper
                .replace("_DIAMOND_", "_DIAMOND_")
                .replace(" ", "_");

            try {
                return Material.valueOf(normalized);
            } catch (IllegalArgumentException e2) {
                // Try legacy lookup
                return Material.matchMaterial(name);
            }
        }
    }
}
