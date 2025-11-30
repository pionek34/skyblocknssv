package pl.nssv.skyblock.commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.utils.ChatUtil;

public class DiscordCommand implements CommandExecutor {
    
    private final SkyblocknNSSV plugin;
    
    public DiscordCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Ta komenda moze byc uzyta tylko przez gracza!");
            return true;
        }
        
        Player player = (Player) sender;
        
        player.sendMessage("");
        String msg = plugin.getConfig().getString("Utils2.ClickDiscord");
        msg = msg.replace("{LINK}", plugin.getConfig().getString("Global.DiscordLink"));
        player.sendMessage(ChatUtil.color(msg));
        player.sendMessage("");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
        
        return true;
    }
}
