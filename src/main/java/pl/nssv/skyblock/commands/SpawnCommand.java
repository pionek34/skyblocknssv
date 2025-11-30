package pl.nssv.skyblock.commands;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;

public class SpawnCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    private Location spawnLocation;
    
    public SpawnCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        String objective = plugin.getObjectiveManager().getObjective(player);
        
        if (objective.equals("TalkWithJerry1") || objective.equals("Wait1")) {
            player.sendMessage(ChatColor.RED + "Complete your objective first!");
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
            return true;
        }
        
        if (objective.contains("Collect4Logs") || objective.contains("Craft16Planks") || 
            objective.contains("BuildOverIsland")) {
            player.sendMessage(ChatColor.RED + "Complete basic tasks first!");
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
        } else {
            if (spawnLocation != null) {
                player.teleport(spawnLocation);
                player.sendMessage(ChatColor.GREEN + "Teleported to spawn!");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            } else {
                player.sendMessage(ChatColor.RED + "Spawn not set!");
            }
        }
        
        return true;
    }
    
    public void setSpawn(Location location) {
        this.spawnLocation = location;
    }
    
    public Location getSpawn() {
        return spawnLocation;
    }
}
