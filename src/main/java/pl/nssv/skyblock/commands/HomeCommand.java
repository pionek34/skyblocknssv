package pl.nssv.skyblock.commands;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
public class HomeCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    public HomeCommand(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // TODO: Implementacja Home
        return true;
    }
}
