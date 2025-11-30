package pl.nssv.skyblock.commands;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
public class LevelCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    public LevelCommand(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        //plugin.getLevelManager().openLevelGUI((Player) sender, 1);
        return true;
    }
}
