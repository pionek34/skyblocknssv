package pl.nssv.skyblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.utils.ChatUtil;

public class UnmuteCommand implements CommandExecutor {
    
    private final SkyblocknNSSV plugin;
    
    public UnmuteCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gens.mute")) {
            sender.sendMessage(ChatUtil.color("&cNie masz uprawnien!"));
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("Utils.UnMute.Select")));
            if (sender instanceof Player) {
                ((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 2.0f);
            }
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatUtil.color("&cGracz nie jest online!"));
            return true;
        }
        
        if (!plugin.getMuteManager().isMuted(target)) {
            sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("Utils.UnMute.NotMuted")));
            if (sender instanceof Player) {
                ((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 2.0f);
            }
            return true;
        }
        
        plugin.getMuteManager().unmutePlayer(target);
        
        sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("Utils.UnMute.Success")));
        
        target.sendMessage("");
        target.sendMessage(ChatUtil.color(plugin.getConfig().getString("Utils.Mute.UnMuted")));
        target.sendMessage("");
        
        if (sender instanceof Player) {
            ((Player) sender).playSound(((Player) sender).getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
        }
        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
        
        return true;
    }
}
