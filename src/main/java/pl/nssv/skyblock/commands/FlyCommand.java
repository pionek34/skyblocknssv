package pl.nssv.skyblock.commands;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;

public class FlyCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;

    public FlyCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        
        if (plugin.getGlobalFlyManager().isGlobalFlyActive()) {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.sendMessage(ChatColor.GREEN + "Fly enabled!");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
            return true;
        }
        
        if (player.hasPermission("essentials.fly")) {
            player.setAllowFlight(!player.getAllowFlight());
            player.sendMessage(player.getAllowFlight() ? 
                ChatColor.GREEN + "Fly enabled!" : 
                ChatColor.RED + "Fly disabled!");
            return true;
        }
        
        player.sendMessage(ChatColor.RED + "You don't have permission to use fly!");
        return true;
    }
}
