package pl.nssv.skyblock.commands;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;

public class DungeonAdminCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    
    public DungeonAdminCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("dungeon.admin")) {
            sender.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "Dungeon Admin Commands:");
            sender.sendMessage(ChatColor.YELLOW + "/dungeonadmin start - Start dungeons");
            sender.sendMessage(ChatColor.YELLOW + "/dungeonadmin stop - Stop dungeons");
            sender.sendMessage(ChatColor.YELLOW + "/dungeonadmin setspawn - Set dungeon spawn");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("start")) {
            plugin.getDungeonManager().startDungeon();
            sender.sendMessage(ChatColor.GREEN + "Dungeons opened!");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("stop")) {
            plugin.getDungeonManager().closeDungeons();
            sender.sendMessage(ChatColor.GREEN + "Dungeons closed!");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("setspawn")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players!");
                return true;
            }
            
            Player player = (Player) sender;
            plugin.getDungeonManager().setSpawn(player.getLocation());
            sender.sendMessage(ChatColor.GREEN + "Dungeon spawn set!");
            return true;
        }
        
        return false;
    }
}
