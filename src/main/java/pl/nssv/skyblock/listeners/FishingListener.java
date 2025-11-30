package pl.nssv.skyblock.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import pl.nssv.skyblock.SkyblocknNSSV;

public class FishingListener implements Listener {
    private final SkyblocknNSSV plugin;

    public FishingListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        
        Player player = event.getPlayer();
        event.setCancelled(true);
        
        // Pobierz nagrodę
        ItemStack reward = plugin.getFishingManager().getRandomFish();
        
        // Start fishing minigame
        plugin.getFishingManager().startFishing(player, reward);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_AIR && 
            event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;
        
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (item.getType() != Material.FISHING_ROD) return;
        
        if (plugin.getFishingManager().isFishing(player)) {
            event.setCancelled(true);
            
            plugin.getFishingManager().addInput(player, "R");
        }
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getAction() != org.bukkit.event.block.Action.LEFT_CLICK_AIR && 
            event.getAction() != org.bukkit.event.block.Action.LEFT_CLICK_BLOCK) return;
        
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (item.getType() != Material.FISHING_ROD) return;
        
        if (plugin.getFishingManager().isFishing(player)) {
            event.setCancelled(true);
            
            plugin.getFishingManager().addInput(player, "L");
        }
    }

    @EventHandler
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        
        if (plugin.getFishingManager().isFishing(player)) {
            plugin.getFishingManager().cancelFishing(player);
        }
    }
}
