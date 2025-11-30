package pl.nssv.skyblock.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.nssv.skyblock.SkyblocknNSSV;

public class PlayerWelcomeListener implements Listener {
    private final SkyblocknNSSV plugin;

    public PlayerWelcomeListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Welcome messages z Utils4
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage("");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getConfig().getString("Utils4.WelcomeMess1")));
            player.sendMessage("");
            
            String mess2 = plugin.getConfig().getString("Utils4.WelcomeMess2");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                mess2.replace("{ONLINE}", String.valueOf(Bukkit.getOnlinePlayers().size()))));
            
            player.sendMessage("");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getConfig().getString("Utils4.WelcomeMess3")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getConfig().getString("Utils4.WelcomeMess4")));
            player.sendMessage("");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getConfig().getString("Utils4.WelcomeMess5")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getConfig().getString("Utils4.WelcomeMess6")));
            player.sendMessage("");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getConfig().getString("Utils4.WelcomeMess7")));
            player.sendMessage("");
            
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
        }, 20L);
        
        // VIP Join Message z Utils3
        if (plugin.getConfig().getBoolean("Utils3.RanksJoinMessage")) {
            if (player.hasPermission("join.message")) {
                String rank = ""; // TODO: Pobierz z PlaceholderAPI
                String joinMsg = plugin.getConfig().getString("Utils3.JoinMessage");
                joinMsg = joinMsg.replace("{RANK}", rank).replace("{PLAYER}", player.getName());
                
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.equals(player)) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', joinMsg));
                    }
                }
            }
        }
    }
}
