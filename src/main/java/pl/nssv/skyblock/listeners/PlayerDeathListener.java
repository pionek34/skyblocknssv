package pl.nssv.skyblock.listeners;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.nssv.skyblock.SkyblocknNSSV;
public class PlayerDeathListener implements Listener {
    private final SkyblocknNSSV plugin;
    public PlayerDeathListener(SkyblocknNSSV plugin) { this.plugin = plugin; }
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.setDeathMessage("");
    }
}
