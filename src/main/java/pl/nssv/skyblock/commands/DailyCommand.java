package pl.nssv.skyblock.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;

public class DailyCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;

    public DailyCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        
        // Sprawdź poziom 3
        int level = plugin.getDataManager().getPlayerData(player).getLevel();
        if (level < 3) {
            String message = plugin.getConfig().getString("Global.TooLowLevel", "&cYou need level {LEVEL} to use this!");
            message = ChatColor.translateAlternateColorCodes('&', message.replace("{LEVEL}", "3"));
            player.sendMessage(message);
            return true;
        }
        
        plugin.getMissionManager().openMissionsGUI(player);
        return true;
    }
}
