package pl.nssv.skyblock.commands;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
public class CoinflipCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    public CoinflipCommand(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // TODO: Implementacja Coinflip
        return true;
    }
}
