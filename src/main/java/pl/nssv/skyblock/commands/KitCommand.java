package pl.nssv.skyblock.commands;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
public class KitCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    public KitCommand(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if (args.length > 0 && args[0].equalsIgnoreCase("claim")) {
            String kit = args.length > 1 ? args[1] : "Default";
            p.performCommand("playerkits claim " + kit);
        } else {
            p.sendMessage("§7GUI kitów (w budowie)");
        }
        return true;
    }
}
