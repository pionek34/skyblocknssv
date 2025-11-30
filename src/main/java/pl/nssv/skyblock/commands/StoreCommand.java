package pl.nssv.skyblock.commands;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
public class StoreCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    public StoreCommand(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        String link = plugin.getConfig().getString("Global.StoreLink");
        p.sendMessage("");
        p.sendMessage("§7Link do sklepu: §6" + link);
        p.sendMessage("");
        return true;
    }
}
