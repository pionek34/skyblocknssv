package pl.nssv.skyblock.commands;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;

public class TutorialCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    
    public TutorialCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.getWorld().getName().equals("world")) {
            player.sendMessage(ChatColor.RED + "You can only use this in the main world!");
            return true;
        }
        
        //player.sendActionBar(ChatColor.YELLOW + "Cinematic started!");
        plugin.getCinematicManager().startCinematic(player, "Welcome", false);
        
        return true;
    }
}
