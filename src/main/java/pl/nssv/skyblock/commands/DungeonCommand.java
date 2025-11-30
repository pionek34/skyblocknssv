package pl.nssv.skyblock.commands;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;

public class DungeonCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    
    public DungeonCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check if player can join
        if (plugin.getDungeonManager() == null) {
            player.sendMessage(ChatColor.RED + "Dungeons system is not available!");
            return true;
        }
        
        if (!plugin.getDungeonManager().isDungeonsOpen()) {
            player.sendMessage(ChatColor.RED + "Dungeons are currently closed!");
            player.sendMessage(ChatColor.YELLOW + "Check back later!");
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
            return true;
        }
        
        // Join dungeon
        if (plugin.getDungeonManager().joinDungeon(player)) {
            player.sendMessage(ChatColor.GREEN + "You joined the dungeon!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        }
        
        return true;
    }
}
