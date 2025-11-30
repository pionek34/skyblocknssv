package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
import java.util.*;

public class WarpManager {
    private final SkyblocknNSSV plugin;
    private final Map<String, Location> warps = new HashMap<>();
    
    public WarpManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    public void setWarp(String name, Location location) {
        warps.put(name, location);
    }
    
    public void deleteWarp(String name) {
        warps.remove(name);
    }
    
    public void warpTo(Player player, String warpName) {
        Location loc = warps.get(warpName);
        
        if (loc == null) {
            player.sendMessage(ChatColor.RED + "Warp not found!");
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
            return;
        }
        
        player.closeInventory();
        
        // Teleport animation
        playAnimation(player);
        
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(loc);
                player.sendMessage(ChatColor.GREEN + "Teleported to " + ChatColor.GOLD + warpName + "!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 25, 0.2, 0.2, 0.2, 0);
            }
        }.runTaskLater(plugin, 10L);
    }
    
    private void playAnimation(Player player) {
        // Placeholder for animation
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
    }
    
    public Set<String> getWarpNames() {
        return warps.keySet();
    }
    
    public boolean warpExists(String name) {
        return warps.containsKey(name);
    }
}
