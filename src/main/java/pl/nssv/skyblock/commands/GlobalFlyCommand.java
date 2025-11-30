package pl.nssv.skyblock.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;

public class GlobalFlyCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;

    public GlobalFlyCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        plugin.getGlobalFlyManager().openGlobalFlyGUI(player);
        
        return true;
    }
}
