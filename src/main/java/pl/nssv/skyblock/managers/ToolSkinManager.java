package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.data.PlayerData;
import java.util.*;

public class ToolSkinManager {
    private final SkyblocknNSSV plugin;
    private final Map<UUID, String> activeSkins = new HashMap<>();
    private final Map<UUID, Set<String>> ownedSkins = new HashMap<>();

    public ToolSkinManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    public void openToolSkinsGUI(Player player) {
        String title = ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("GUIS.ToolSkins.Default", "&8Tool Skins"));
        org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, 54, title);
        
        String[] categories = {"Pickaxe", "Axe", "Shovel", "Hoe"};
        int[] slots = {20, 22, 24, 26};
        
        for (int i = 0; i < categories.length; i++) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(7020 + i);
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("GUIS.ToolSkins.Category." + categories[i], categories[i])));
            item.setItemMeta(meta);
            inv.setItem(slots[i], item);
        }
        
        player.openInventory(inv);
    }

    public void openCategoryGUI(Player player, String category) {
        String title = ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("GUIS.ToolSkins." + category, "&8" + category));
        org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, 54, title);
        
        String[] skins = getSkinsList(category);
        int slot = 0;
        
        for (String skin : skins) {
            if (slot >= 45) break;
            
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(7030 + slot);
            
            boolean owned = hasSkin(player, skin);
            String name = plugin.getConfig().getString("GUIS.ToolSkins.Skin." + skin + ".Name", skin);
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            
            List<String> lore = new ArrayList<>();
            if (owned) {
                lore.add(ChatColor.GREEN + "✓ Owned");
                if (isActiveSkin(player, skin)) {
                    lore.add(ChatColor.GOLD + "★ Active");
                } else {
                    lore.add(ChatColor.GRAY + "Click to activate");
                }
            } else {
                int price = plugin.getConfig().getInt("GUIS.ToolSkins.Skin." + skin + ".Price", 1000);
                lore.add(ChatColor.RED + "✗ Not owned");
                lore.add(ChatColor.YELLOW + "Price: " + price + " gems");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            
            inv.setItem(slot, item);
            slot++;
        }
        
        player.openInventory(inv);
    }

    private String[] getSkinsList(String category) {
        return new String[]{"Fire", "Ice", "Lightning", "Blood", "Rainbow"};
    }

    public boolean hasSkin(Player player, String skin) {
        return ownedSkins.getOrDefault(player.getUniqueId(), new HashSet<>()).contains(skin);
    }

    public boolean isActiveSkin(Player player, String skin) {
        return skin.equals(activeSkins.get(player.getUniqueId()));
    }

    public void purchaseSkin(Player player, String skin) {
        if (hasSkin(player, skin)) {
            player.sendMessage(ChatColor.RED + "You already own this skin!");
            return;
        }
        
        int price = plugin.getConfig().getInt("GUIS.ToolSkins.Skin." + skin + ".Price", 1000);
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        
        if (data.getGems() < price) {
            player.sendMessage(ChatColor.RED + "Not enough gems!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 2.0f);
            return;
        }
        
        data.setGems(data.getGems() - price);
        ownedSkins.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(skin);
        
        player.sendMessage(ChatColor.GREEN + "Purchased skin: " + skin);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
    }

    public void activateSkin(Player player, String skin) {
        if (!hasSkin(player, skin)) {
            player.sendMessage(ChatColor.RED + "You don't own this skin!");
            return;
        }
        
        activeSkins.put(player.getUniqueId(), skin);
        player.sendMessage(ChatColor.GREEN + "Activated skin: " + skin);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
    }

    public String getActiveSkin(Player player) {
        return activeSkins.get(player.getUniqueId());
    }

    public void applySkinEffect(Player player, Location location, String tool) {
        String skin = getActiveSkin(player);
        if (skin == null) return;
        
        switch (skin) {
            case "Fire":
                location.getWorld().spawnParticle(Particle.FLAME, location, 5, 0.2, 0.2, 0.2, 0.02);
                break;
            case "Ice":
                location.getWorld().spawnParticle(Particle.SNOWFLAKE, location, 5, 0.2, 0.2, 0.2, 0.02);
                break;
            case "Lightning":
                location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, location, 3, 0.1, 0.1, 0.1, 0.02);
                break;
            case "Blood":
                //location.getWorld().spawnParticle(Particle.REDSTONE, location, 5, 0.2, 0.2, 0.2,
                    //new Particle.DustOptions(Color.RED, 1.0f));
                break;
            case "Rainbow":
                Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE};
                Color color = colors[new Random().nextInt(colors.length)];
                //location.getWorld().spawnParticle(Particle.REDSTONE, location, 3, 0.2, 0.2, 0.2,
                    //new Particle.DustOptions(color, 1.0f));
                break;
        }
    }
}
