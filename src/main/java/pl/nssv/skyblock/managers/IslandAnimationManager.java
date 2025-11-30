package pl.nssv.skyblock.managers;
import org.bukkit.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
public class IslandAnimationManager {
    private final SkyblocknNSSV plugin;
    public IslandAnimationManager(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public void startIslandCreationAnimation(Player player, boolean create) {
        player.sendMessage(ChatColor.GREEN + "Island animation!");
    }
}
