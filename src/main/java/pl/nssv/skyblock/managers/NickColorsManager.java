package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.data.PlayerData;
import java.util.*;

public class NickColorsManager {
    private final SkyblocknNSSV plugin;
    private final Map<UUID, String> activeColors = new HashMap<>();
    private final Map<UUID, Boolean> boldEnabled = new HashMap<>();
    private final Map<UUID, Set<String>> ownedColors = new HashMap<>();

    public NickColorsManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    public void openNickGUI(Player player) {
        String title = ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("GUIS.Nick.Default", "&8Nick Colors"));
        org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, 54, title);
        
        String[] goldColors = {"&6", "&e", "&c&6", "&e&6", "&6&c", "&6&e"};
        String[] goldNames = {"Gold", "Yellow", "Red-Gold", "Yellow-Gold", "Gold-Red", "Gold-Yellow"};
        
        for (int i = 0; i < goldColors.length; i++) {
            ItemStack item = new ItemStack(Material.GOLD_INGOT);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', goldColors[i] + goldNames[i]));
            
            List<String> lore = new ArrayList<>();
            boolean owned = hasColor(player, "gold", goldColors[i]);
            
            if (owned) {
                lore.add(ChatColor.GREEN + "✓ Owned");
            } else {
                lore.add(ChatColor.RED + "✗ Requires Gold rank");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(10 + i, item);
        }
        
        String[] diamondColors = {
            "&b", "&3", "&9&b", "&b&9", "&b&3", "&3&b",
            "&b&f", "&f&b", "&9&3&b", "&b&3&9", "&3&9&b", "&9&b&3", "&3&b&9", "&b&9&3"
        };
        
        for (int i = 0; i < Math.min(diamondColors.length, 14); i++) {
            ItemStack item = new ItemStack(Material.DIAMOND);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', diamondColors[i] + "Diamond " + (i+1)));
            
            List<String> lore = new ArrayList<>();
            boolean owned = hasColor(player, "diamond", diamondColors[i]);
            
            if (owned) {
                lore.add(ChatColor.GREEN + "✓ Owned");
            } else {
                lore.add(ChatColor.RED + "✗ Requires Diamond rank");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(19 + i, item);
        }
        
        ItemStack boldItem = new ItemStack(Material.NAME_TAG);
        ItemMeta boldMeta = boldItem.getItemMeta();
        boldMeta.setDisplayName(ChatColor.BOLD + "Bold Text");
        List<String> boldLore = new ArrayList<>();
        if (isBoldEnabled(player)) {
            boldLore.add(ChatColor.GREEN + "★ Enabled");
        } else {
            boldLore.add(ChatColor.GRAY + "Click to enable");
        }
        boldMeta.setLore(boldLore);
        boldItem.setItemMeta(boldMeta);
        inv.setItem(49, boldItem);
        
        player.openInventory(inv);
    }

    public boolean hasColor(Player player, String rank, String color) {
        if (rank.equals("gold")) {
            return player.hasPermission("nick.gold") || player.hasPermission("nick.diamond");
        } else if (rank.equals("diamond")) {
            return player.hasPermission("nick.diamond");
        }
        return false;
    }

    public void setNickColor(Player player, String color) {
        activeColors.put(player.getUniqueId(), color);
        updateDisplayName(player);
        player.sendMessage(ChatColor.GREEN + "Nick color updated!");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
    }

    public void toggleBold(Player player) {
        boolean current = boldEnabled.getOrDefault(player.getUniqueId(), false);
        boldEnabled.put(player.getUniqueId(), !current);
        updateDisplayName(player);
        player.sendMessage(ChatColor.GREEN + "Bold: " + (!current ? "enabled" : "disabled"));
    }

    public boolean isBoldEnabled(Player player) {
        return boldEnabled.getOrDefault(player.getUniqueId(), false);
    }

    public void updateDisplayName(Player player) {
        String color = activeColors.getOrDefault(player.getUniqueId(), "&7");
        boolean bold = boldEnabled.getOrDefault(player.getUniqueId(), false);
        
        String icon = plugin.getPrefixIconManager().getActiveIcon(player);
        String prefix = plugin.getPrefixIconManager().getActivePrefix(player);
        
        String displayName = ChatColor.translateAlternateColorCodes('&',
            icon + " " + prefix + (bold ? "&l" : "") + color + player.getName());
        
        player.setDisplayName(displayName);
        player.setPlayerListName(displayName);
    }

    public String applyGradient(String text, String startColor, String endColor) {
        // Simple gradient implementation
        return ChatColor.translateAlternateColorCodes('&', startColor + text);
    }
}
