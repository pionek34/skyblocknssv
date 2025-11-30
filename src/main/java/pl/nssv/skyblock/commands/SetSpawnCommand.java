package pl.nssv.skyblock.commands;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;

public class SetSpawnCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    
    public SetSpawnCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players!");
            return true;
        }
        
        if (!sender.hasPermission("skyblock.setspawn")) {
            sender.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        Player player = (Player) sender;
        plugin.getSpawnCommand().setSpawn(player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Spawn set!");
        
        return true;
    }
}
