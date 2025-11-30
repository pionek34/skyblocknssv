package pl.nssv.skyblock.commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.utils.ChatUtil;

public class XqEffectsCommand implements CommandExecutor {
    
    private final SkyblocknNSSV plugin;
    
    public XqEffectsCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Ta komenda moze byc uzyta tylko przez gracza!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("*")) {
            player.sendMessage(ChatUtil.color("&cNie masz uprawnien!"));
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage("");
            player.sendMessage(ChatUtil.color(" &6/xqEffects &eholo &3- &7Utworz hologram przy patrzonym bloku"));
            player.sendMessage(ChatUtil.color(" &6/xqEffects &ekillholo &3- &7Usun hologram"));
            player.sendMessage("");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("holo")) {
            if (player.getTargetBlockExact(5) == null) {
                player.sendMessage(ChatUtil.color("&cMusisz patrzec na blok!"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 2.0f);
                return true;
            }
            
            plugin.getHologramManager().createHologram(player.getTargetBlockExact(5).getLocation().add(0.5, 1, 0.5));
            player.sendMessage(ChatUtil.color("<##8fff00>Sukces!"));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
            return true;
        }
        
        if (args[0].equalsIgnoreCase("killholo")) {
            plugin.getHologramManager().removeHologram();
            player.sendMessage(ChatUtil.color("<##8fff00>Usunieto hologram!"));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
            return true;
        }
        
        player.sendMessage(ChatUtil.color("<##ff4545>Nieznany argument!"));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 2.0f);
        return true;
    }
}
