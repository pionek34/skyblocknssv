package pl.nssv.skyblock.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.nssv.skyblock.SkyblocknNSSV;

public class LevelGUIListener implements Listener {
    private final SkyblocknNSSV plugin;

    public LevelGUIListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        String title = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("GUIS.Level.Default"));
        
        if (!event.getView().getTitle().equals(title)) return;
        
        event.setCancelled(true);
        
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory() == event.getWhoClicked().getInventory()) return;
        
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        
        if (slot == 39) {
            // Previous page
            //plugin.getLevelManager().openLevelGUI(player, 1); // TODO: Track current page
        } else if (slot == 43) {
            // Next page
            //plugin.getLevelManager().openLevelGUI(player, 2); // TODO: Track current page
        }
    }
}
