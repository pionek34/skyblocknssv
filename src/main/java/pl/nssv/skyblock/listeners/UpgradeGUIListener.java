package pl.nssv.skyblock.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import pl.nssv.skyblock.SkyblocknNSSV;

public class UpgradeGUIListener implements Listener {
    private final SkyblocknNSSV plugin;

    public UpgradeGUIListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        String title = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("GUIS.Upgrades.Default"));
        
        if (!event.getView().getTitle().equals(title)) return;
        
        event.setCancelled(true);
        
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory() == event.getWhoClicked().getInventory()) return;
        
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        
        if (slot == 2 || slot == 4 || slot == 6 || slot == 21 || slot == 23) {
            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            
            // TODO: Implementacja zakupu upgrade
            player.sendMessage(ChatColor.GREEN + "Funkcja zakupu w trakcie implementacji!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        }
    }
}
