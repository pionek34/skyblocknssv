package pl.nssv.skyblock.commands;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
public class HealCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    public HealCommand(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if (!p.hasPermission("essentials.heal")) {
            p.sendMessage("§cBrak uprawnień!");
            return true;
        }
        p.setHealth(20);
        p.setFoodLevel(20);
        p.sendMessage("§aPomyślnie uleczono!");
        return true;
    }
}
