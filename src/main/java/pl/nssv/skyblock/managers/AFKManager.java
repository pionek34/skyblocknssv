package pl.nssv.skyblock.managers;
import org.bukkit.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
public class AFKManager {
    private final SkyblocknNSSV plugin;
    public AFKManager(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public void enterAFKArea(Player player) {
        player.sendMessage(ChatColor.GREEN + "Entered AFK Area");
    }
    public void openAFKGUI(Player player) {
        player.openInventory(Bukkit.createInventory(null, 27, "AFK Area"));
    }
}
