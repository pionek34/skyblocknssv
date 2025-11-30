package pl.nssv.skyblock.listeners;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import pl.nssv.skyblock.SkyblocknNSSV;
public class SpawnerHopperListener implements Listener {
    private final SkyblocknNSSV plugin;
    public SpawnerHopperListener(SkyblocknNSSV plugin) { this.plugin = plugin; }
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Material type = e.getBlock().getType();
        if (type.toString().contains("SPAWNER") || type == Material.HOPPER) {
            e.getPlayer().sendMessage(ChatColor.GREEN + "Placed " + type);
        }
    }
}
