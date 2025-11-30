package pl.nssv.skyblock.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.nssv.skyblock.SkyblocknNSSV;

public class AllGUIListeners implements Listener {
    private final SkyblocknNSSV plugin;

    public AllGUIListeners(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGUIClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = ChatColor.stripColor(event.getView().getTitle());
        
        // Tool Skins GUI
        if (title.contains("Tool Skins")) {
            event.setCancelled(true);
            if (event.getClickedInventory() == player.getInventory()) return;
            // Handle click
        }
        
        // Icons GUI
        else if (title.contains("Icons")) {
            event.setCancelled(true);
            if (event.getClickedInventory() == player.getInventory()) return;
            // Handle icon selection
        }
        
        // Nick Colors GUI
        else if (title.contains("Nick")) {
            event.setCancelled(true);
            if (event.getClickedInventory() == player.getInventory()) return;
            // Handle color selection
        }
    }
}
