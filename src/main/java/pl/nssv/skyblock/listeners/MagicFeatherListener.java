package pl.nssv.skyblock.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nssv.skyblock.SkyblocknNSSV;
import java.util.*;

public class MagicFeatherListener implements Listener {
    private final SkyblocknNSSV plugin;
    private final Map<UUID, Long> flyEndTimes = new HashMap<>();

    public MagicFeatherListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
        startFlyChecker();
    }

    @EventHandler
    public void onMagicFeatherUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || !item.hasItemMeta()) return;
        if (!item.getItemMeta().hasCustomModelData()) return;
        
        int modelData = item.getItemMeta().getCustomModelData();
        if (modelData != 6998) return; // Magic Feather model data
        
        event.setCancelled(true);
        
        if (flyEndTimes.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You already have fly active!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 2.0f);
            return;
        }
        
        item.setAmount(item.getAmount() - 1);
        
        player.setAllowFlight(true);
        player.setFlying(true);
        
        long endTime = System.currentTimeMillis() + (30 * 60 * 1000); // 30 minut
        flyEndTimes.put(player.getUniqueId(), endTime);
        
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("Utils3.MagicFeatherActivated", "&aActivated fly for 30 minutes!")));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
        
        // Particle effect
        player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
    }

    private void startFlyChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Iterator<Map.Entry<UUID, Long>> iterator = flyEndTimes.entrySet().iterator();
                
                while (iterator.hasNext()) {
                    Map.Entry<UUID, Long> entry = iterator.next();
                    Player player = Bukkit.getPlayer(entry.getKey());
                    
                    if (player == null || !player.isOnline()) {
                        iterator.remove();
                        continue;
                    }
                    
                    if (now >= entry.getValue()) {
                        player.setAllowFlight(false);
                        player.setFlying(false);
                        
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.getConfig().getString("Utils3.MagicFeatherExpired", "&cYour fly time has expired!")));
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.5f);
                        
                        iterator.remove();
                    }
                }
            }
        }.runTaskTimer(plugin, 200L, 200L);
    }
}
