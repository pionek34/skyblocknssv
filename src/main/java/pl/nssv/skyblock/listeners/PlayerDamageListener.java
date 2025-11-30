package pl.nssv.skyblock.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.data.PlayerData;

public class PlayerDamageListener implements Listener {
    
    private final SkyblocknNSSV plugin;
    
    public PlayerDamageListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        
        // Sprawdz immunity od fall damage
        if (data.hasImmunity() && event.getCause() == DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }
        
        // Sprawdz void damage w swiecie skyblock
        if (event.getCause() == DamageCause.VOID) {
            event.setCancelled(true);
            
            if (player.getWorld().getName().equals("bskyblock_world")) {
                if (!data.hasImmunity()) {
                    data.setImmunity(true);
                    
                    // Wykonaj komende skyblock go
                    player.performCommand("bskyblock:skyblock go");
                    
                    // Usun immunity po 1 sekundzie
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        data.setImmunity(false);
                    }, 20L);
                }
            }
        }
    }
}
