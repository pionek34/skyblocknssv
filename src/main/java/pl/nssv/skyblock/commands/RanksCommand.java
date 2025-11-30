package pl.nssv.skyblock.commands;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
public class RanksCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    public RanksCommand(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        ((Player) sender).sendMessage("§7GUI rang VIP (w budowie)");
        return true;
    }
}
