package pl.nssv.skyblock.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.nssv.skyblock.SkyblocknNSSV;

public class SpawnProtectionListener implements Listener {
    private final SkyblocknNSSV plugin;

    public SpawnProtectionListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        
        // Check if player is in spawn region
        // W skrypcie to było: if victim is in region "spawn"
        // Musisz mieć zainstalowany WorldGuard aby to działało
        // Na razie po prostu sprawdzamy dystans od spawnu
        
        if (player.getWorld().getSpawnLocation().distance(player.getLocation()) < 50) {
            event.setCancelled(true);
        }
    }
}
