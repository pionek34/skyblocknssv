package pl.nssv.skyblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.utils.ChatUtil;

public class MuteCommand implements CommandExecutor {
    
    private final SkyblocknNSSV plugin;
    
    public MuteCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gens.mute")) {
            sender.sendMessage(ChatUtil.color("&cNie masz uprawnien!"));
            return true;
        }
        
        if (args.length < 2) {
            if (args.length == 0) {
                sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("Utils.Mute.Select")));
            } else {
                sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("Utils.Mute.Duration")));
            }
            
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
        
        // Parsuj czas
        long duration = plugin.getMuteManager().parseTime(args[1]);
        if (duration == 0) {
            sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("Utils.Mute.IncorrectDuration")));
            if (sender instanceof Player) {
                ((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 2.0f);
            }
            return true;
        }
        
        // Sprawdz czy juz jest wyciszony
        if (plugin.getMuteManager().isMuted(target)) {
            sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("Utils.Mute.AlreadyMuted")));
            if (sender instanceof Player) {
                ((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 2.0f);
            }
            return true;
        }
        
        // Wycisz gracza
        plugin.getMuteManager().mutePlayer(target, duration);
        
        // Wiadomosc dla admina
        String timeFormatted = plugin.getMuteManager().formatTime(duration);
        sender.sendMessage("");
        String msg = plugin.getConfig().getString("Utils.Mute.Success");
        msg = msg.replace("{PLAYER}", target.getName());
        msg = msg.replace("{TIME}", timeFormatted);
        sender.sendMessage(ChatUtil.color(msg));
        sender.sendMessage("");
        
        // Wiadomosc dla gracza
        target.sendMessage("");
        msg = plugin.getConfig().getString("Utils.Mute.Muted");
        msg = msg.replace("{TIME}", timeFormatted);
        target.sendMessage(ChatUtil.color(msg));
        target.sendMessage("");
        
        target.playSound(target.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1.0f, 0.7f);
        
        if (sender instanceof Player) {
            ((Player) sender).playSound(((Player) sender).getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
        }
        
        return true;
    }
}
