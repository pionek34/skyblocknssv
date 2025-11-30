package pl.nssv.skyblock.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.nssv.skyblock.SkyblocknNSSV;

public class GlobalFlyListener implements Listener {
    private final SkyblocknNSSV plugin;

    public GlobalFlyListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = ChatColor.stripColor(event.getView().getTitle());
        String expectedTitle = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("GUIS.GlobalFly.Default", "Global Fly")));
        
        if (!title.equals(expectedTitle)) return;
        
        event.setCancelled(true);
        
        if (event.getClickedInventory() == event.getWhoClicked().getInventory()) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        
        int[] slots = {11, 12, 13, 14, 15};
        int[] amounts = {1000, 5000, 10000, 25000, 100000};
        
        for (int i = 0; i < slots.length; i++) {
            if (slot == slots[i]) {
                int amount = amounts[i];
                
                if (plugin.getEconomy().has(player, amount)) {
                    plugin.getEconomy().withdrawPlayer(player, amount);
                    plugin.getGlobalFlyManager().depositMoney(player, amount);
                    player.closeInventory();
                } else {
                    String message = plugin.getConfig().getString("Global.NotEnough", "&cYou don't have enough money!");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 2.0f);
                    player.closeInventory();
                }
                
                return;
            }
        }
    }
}
