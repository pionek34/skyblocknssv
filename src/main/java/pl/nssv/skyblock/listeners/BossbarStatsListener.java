package pl.nssv.skyblock.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import pl.nssv.skyblock.SkyblocknNSSV;

public class BossbarStatsListener implements Listener {
    private final SkyblocknNSSV plugin;
    
    public BossbarStatsListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Remove any existing bossbars
        plugin.getBossbarStatsManager().removeBossbars(player);
        
        // Teleport to spawn if configured
        Location spawn = plugin.getSpawnCommand().getSpawn();
        if (spawn != null) {
            player.teleport(spawn);
        }
        
        // Delay bossbar creation for Bedrock players
        if (player.getName().startsWith(".")) {
            new org.bukkit.scheduler.BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getBossbarStatsManager().createBossbars(player);
                }
            }.runTaskLater(plugin, 20L);
        } else {
            // Regular player - wait for resource pack
            new org.bukkit.scheduler.BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getBossbarStatsManager().createBossbars(player);
                }
            }.runTaskLater(plugin, 20L);
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getBossbarStatsManager().removeBossbars(player);
    }
    
    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        
        if (player.getName().startsWith(".")) {
            return; // Bedrock player
        }
        
        PlayerResourcePackStatusEvent.Status status = event.getStatus();
        
        if (status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            // Pack loaded successfully
            sendPackLoadedTitle(player);
            
            new org.bukkit.scheduler.BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getBossbarStatsManager().createBossbars(player);
                }
            }.runTaskLater(plugin, 20L);
        } else if (status == PlayerResourcePackStatusEvent.Status.ACCEPTED) {
            // Pack is downloading
            sendPackLoadingTitle(player);
        } else if (status == PlayerResourcePackStatusEvent.Status.DECLINED || 
                   status == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            // Pack was declined or failed
            player.kickPlayer(ChatColor.RED + "Resource pack is required to play on this server!");
        }
    }
    
    private void sendPackLoadingTitle(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
        
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.2f);
            }
        }.runTaskLater(plugin, 2L);
        
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.5f);
            }
        }.runTaskLater(plugin, 4L);
        
        player.sendTitle(
            ChatColor.YELLOW + "Loading Resource Pack...",
            ChatColor.GRAY + "Please wait",
            0, 600, 10
        );
    }
    
    private void sendPackLoadedTitle(Player player) {
        player.sendTitle("", "", 0, 60, 10); // Clear title
    }
}
