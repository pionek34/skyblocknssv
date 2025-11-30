package pl.nssv.skyblock.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.nssv.skyblock.SkyblocknNSSV;

public class PlayerQuitListener implements Listener {
    
    private final SkyblocknNSSV plugin;
    
    public PlayerQuitListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        
        // Zapisz i wyladuj dane gracza
        plugin.getDataManager().unloadPlayerData(event.getPlayer().getUniqueId());
    }
}
