package pl.nssv.skyblock.listeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import pl.nssv.skyblock.SkyblocknNSSV;

public class CinematicListener implements Listener {
    private final SkyblocknNSSV plugin;
    
    public CinematicListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Clear any stuck cinematic state
        if (plugin.getCinematicManager().isInCinematic(player)) {
            plugin.getCinematicManager().handleSkip(player);
        }
    }
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getCinematicManager().isInCinematic(player)) {
            return;
        }
        
        // Block commands during cinematic (except specific ones)
        if (player.getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Commands are disabled during cinematic!");
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
        } else {
            int stage = plugin.getCinematicManager().getSkipStage(player);
            if (stage == 7) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Commands are disabled during cinematic!");
                player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
            }
        }
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        // Prevent fall damage during cinematic
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (plugin.getCinematicManager().isInCinematic(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        
        if (player.getGameMode() != GameMode.SPECTATOR) {
            return;
        }
        
        if (!plugin.getCinematicManager().isInCinematic(player)) {
            return;
        }
        
        if (!player.isSneaking()) { // Starting to sneak
            event.setCancelled(true);
            plugin.getCinematicManager().handleSkip(player);
            
            int stage = plugin.getCinematicManager().getSkipStage(player);
            if (stage == 5 || stage == 6) {
                //player.sendActionBar(ChatColor.YELLOW + "Press SHIFT " + stage + " more times to skip");
            }
        }
    }
}
