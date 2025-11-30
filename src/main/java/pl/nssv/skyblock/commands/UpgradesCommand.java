package pl.nssv.skyblock.commands;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
public class UpgradesCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    public UpgradesCommand(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        ((Player) sender).sendMessage("§7GUI ulepszeń wyspy (w budowie)");
        return true;
    }
}
