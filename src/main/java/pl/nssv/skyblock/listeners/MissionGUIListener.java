package pl.nssv.skyblock.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.nssv.skyblock.SkyblocknNSSV;

public class MissionGUIListener implements Listener {
    private final SkyblocknNSSV plugin;

    public MissionGUIListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = ChatColor.stripColor(event.getView().getTitle());
        String expectedTitle = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("GUIS.Missions.Default", "Missions")));
        
        if (!title.equals(expectedTitle)) return;
        
        event.setCancelled(true);
        
        if (event.getClickedInventory() == event.getWhoClicked().getInventory()) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        
        String[] categories = {"Hourly", "Daily", "Weekly"};
        int[] slots = {11, 13, 15};
        
        for (int i = 0; i < slots.length; i++) {
            if (slot == slots[i]) {
                String category = categories[i];
                
                ItemStack clicked = event.getCurrentItem();
                if (clicked == null || !clicked.hasItemMeta()) return;
                
                ItemMeta meta = clicked.getItemMeta();
                
                // Sprawdź czy cooldown (model data 7010)
                if (meta.hasCustomModelData() && meta.getCustomModelData() == 7010) {
                    player.sendMessage(ChatColor.RED + "This mission is currently locked!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 2.0f);
                    return;
                }
                
                //if (!plugin.getMissionManager().hasMissions(player, category)) {
                    // Start mission
                    //if (plugin.getMissionManager().canStartMission(player, category)) {
                        //plugin.getMissionManager().loadMissions(player, category);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
                        player.closeInventory();
                        
                        player.sendMessage("");
                        String mess = plugin.getConfig().getString("Utils3.MissionStart", "&aStarted {MISSION} mission!");
                        String categoryName = plugin.getConfig().getString("GUIS.Missions." + category, category);
                        mess = ChatColor.translateAlternateColorCodes('&', mess.replace("{MISSION}", categoryName));
                        player.sendMessage(mess);
                        player.sendMessage("");
                        
                        String info = plugin.getConfig().getString("Utils3.MissionInfo", "&7Your tasks:");
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', info));
                        
                        // Wyświetl zadania
                        // (będą pokazane w GUI, więc można pominąć tutaj)
                        
                        player.sendMessage("");
                   // } else {
                        player.sendMessage(ChatColor.RED + "Mission is on cooldown!");
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 2.0f);
                    //}
                } else {
                    String mess = plugin.getConfig().getString("Utils3.CompleteToReceive", "&cComplete all tasks to receive rewards!");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', mess));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 2.0f);
                }
                
                return;
            }
        }
   // }
}
