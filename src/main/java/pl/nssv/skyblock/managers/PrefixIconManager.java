package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.data.PlayerData;
import java.util.*;

public class PrefixIconManager {
    private final SkyblocknNSSV plugin;
    private final Map<UUID, String> activeIcons = new HashMap<>();
    private final Map<UUID, String> activePrefixes = new HashMap<>();
    private final Map<UUID, Set<String>> ownedIcons = new HashMap<>();
    private final Map<UUID, Set<String>> ownedPrefixes = new HashMap<>();

    public PrefixIconManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    public void openIconsGUI(Player player) {
        String title = ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("GUIS.Icons.Default", "&8Icons"));
        org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, 54, title);
        
        String[] icons = {"❤", "★", "✦", "◆", "●", "■", "▲", "►", "☀", "☁", "☂", "☃", "♠", "♣", "♥", "♦", "⚡", "⚙", "⚔", "⛏", "✿"};
        
        for (int i = 0; i < Math.min(icons.length, 45); i++) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(7100 + i);
            
            String icon = icons[i];
            boolean owned = hasIcon(player, icon);
            
            meta.setDisplayName(ChatColor.WHITE + icon);
            List<String> lore = new ArrayList<>();
            
            if (owned) {
                lore.add(ChatColor.GREEN + "✓ Owned");
                if (icon.equals(activeIcons.get(player.getUniqueId()))) {
                    lore.add(ChatColor.GOLD + "★ Active");
                } else {
                    lore.add(ChatColor.GRAY + "Click to activate");
                }
            } else {
                int reqLevel = 1 + (i * 2);
                lore.add(ChatColor.RED + "✗ Not owned");
                lore.add(ChatColor.YELLOW + "Required level: " + reqLevel);
            }
            
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        
        player.openInventory(inv);
    }

    public void openPrefixesGUI(Player player) {
        String title = ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("GUIS.Prefixes.Default", "&8Prefixes"));
        org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, 27, title);
        
        String[] colors = {"&c", "&6", "&e", "&a", "&b", "&9", "&5"};
        String[] names = {"Red", "Orange", "Yellow", "Green", "Aqua", "Blue", "Purple"};
        
        for (int i = 0; i < colors.length; i++) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(7200 + i);
            
            boolean owned = hasPrefix(player, colors[i]);
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', colors[i] + names[i]));
            
            List<String> lore = new ArrayList<>();
            if (owned) {
                lore.add(ChatColor.GREEN + "✓ Owned");
                if (colors[i].equals(activePrefixes.get(player.getUniqueId()))) {
                    lore.add(ChatColor.GOLD + "★ Active");
                }
            } else {
                lore.add(ChatColor.RED + "✗ Not owned");
                lore.add(ChatColor.YELLOW + "Price: 500 gems");
            }
            
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(10 + i, item);
        }
        
        player.openInventory(inv);
    }

    public boolean hasIcon(Player player, String icon) {
        int level = plugin.getDataManager().getPlayerData(player).getLevel();
        String[] icons = {"❤", "★", "✦", "◆", "●", "■", "▲", "►", "☀", "☁", "☂", "☃", "♠", "♣", "♥", "♦", "⚡", "⚙", "⚔", "⛏", "✿"};
        for (int i = 0; i < icons.length; i++) {
            if (icons[i].equals(icon)) {
                return level >= (1 + i * 2);
            }
        }
        return false;
    }

    public boolean hasPrefix(Player player, String prefix) {
        return ownedPrefixes.getOrDefault(player.getUniqueId(), new HashSet<>()).contains(prefix);
    }

    public void purchasePrefix(Player player, String prefix) {
        if (hasPrefix(player, prefix)) {
            player.sendMessage(ChatColor.RED + "You already own this prefix!");
            return;
        }
        
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        if (data.getGems() < 500) {
            player.sendMessage(ChatColor.RED + "Not enough gems!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 2.0f);
            return;
        }
        
        data.setGems(data.getGems() - 500);
        ownedPrefixes.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(prefix);
        
        player.sendMessage(ChatColor.GREEN + "Purchased prefix!");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
    }

    public void activateIcon(Player player, String icon) {
        if (!hasIcon(player, icon)) {
            player.sendMessage(ChatColor.RED + "You don't have this icon!");
            return;
        }
        activeIcons.put(player.getUniqueId(), icon);
        player.sendMessage(ChatColor.GREEN + "Activated icon: " + icon);
    }

    public void activatePrefix(Player player, String prefix) {
        if (!hasPrefix(player, prefix)) {
            player.sendMessage(ChatColor.RED + "You don't own this prefix!");
            return;
        }
        activePrefixes.put(player.getUniqueId(), prefix);
        player.sendMessage(ChatColor.GREEN + "Activated prefix!");
    }

    public String getActiveIcon(Player player) {
        return activeIcons.getOrDefault(player.getUniqueId(), "");
    }

    public String getActivePrefix(Player player) {
        return activePrefixes.getOrDefault(player.getUniqueId(), "&7");
    }
}
