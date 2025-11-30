package pl.nssv.skyblock.commands;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.managers.ClanManager;
public class ClanCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    public ClanCommand(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        ClanManager cm = plugin.getClanManager();
        if (args.length == 0) {
            cm.sendHelp(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "create": cm.createClan(player, args.length > 1 ? args[1] : null); break;
            case "delete": case "disband": cm.deleteClan(player); break;
            case "invite": case "add": cm.inviteClan(player, args.length > 1 ? args[1] : null); break;
            case "join": cm.joinClan(player, args.length > 1 ? args[1] : null); break;
            case "leave": case "quit": cm.leaveClan(player); break;
            case "kick": cm.kickClan(player, args.length > 1 ? args[1] : null); break;
            case "info": cm.infoClan(player, args.length > 1 ? args[1] : null); break;
            case "color": case "colors": cm.colorClan(player); break;
            default: cm.sendHelp(player);
        }
        return true;
    }
}
