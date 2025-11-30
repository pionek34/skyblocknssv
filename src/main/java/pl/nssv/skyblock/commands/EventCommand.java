package pl.nssv.skyblock.commands;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
public class EventCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    public EventCommand(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if (!p.hasPermission("*")) { p.sendMessage("§cBrak uprawnień!"); return true; }
        if (args.length < 2) {
            p.sendMessage("§cUżyj: /event <start/stop> <DoubleXP/JobBoost>");
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {
            plugin.getEventManager().startEvent(args[1]);
        } else if (args[0].equalsIgnoreCase("stop")) {
            plugin.getEventManager().stopEvent(args[1]);
        }
        return true;
    }
}
