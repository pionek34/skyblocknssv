package pl.nssv.skyblock.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.data.PlayerData;

public class PlayerJoinListener implements Listener {
    
    private final SkyblocknNSSV plugin;
    
    public PlayerJoinListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        
        // Ukryj hologram
        if (plugin.getHologramManager().getHologramEntity() != null) {
            event.getPlayer().hideEntity(plugin, plugin.getHologramManager().getHologramEntity());
        }
        
        // Sprawdz czy pierwszy join
        PlayerData data = plugin.getDataManager().getPlayerData(event.getPlayer());
        if (!data.getConfig().contains("firstjoin")) {
            data.getConfig().set("firstjoin", true);
            
            // Teleportuj do spawnu jesli jest ustawiony
            if (plugin.getConfig().contains("Spawn")) {
                String world = plugin.getConfig().getString("Spawn.world");
                double x = plugin.getConfig().getDouble("Spawn.x");
                double y = plugin.getConfig().getDouble("Spawn.y");
                double z = plugin.getConfig().getDouble("Spawn.z");
                float yaw = (float) plugin.getConfig().getDouble("Spawn.yaw");
                float pitch = (float) plugin.getConfig().getDouble("Spawn.pitch");
                
                if (Bukkit.getWorld(world) != null) {
                    Location spawn = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                    event.getPlayer().teleport(spawn);
                }
            }
        }
        
        // Usun delay na zmiane pracy
        data.setJobChangeTime(0);
    }
}
