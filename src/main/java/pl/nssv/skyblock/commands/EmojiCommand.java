package pl.nssv.skyblock.commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.utils.ChatUtil;

public class EmojiCommand implements CommandExecutor {
    
    private final SkyblocknNSSV plugin;
    
    public EmojiCommand(SkyblocknNSSV plugin) {
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
        player.sendMessage(ChatUtil.color(plugin.getConfig().getString("Utils.EmojiTitle")));
        player.sendMessage("");
        
        for (int i = 1; i <= 6; i++) {
            player.sendMessage(ChatUtil.color(plugin.getConfig().getString("Utils.Emoji" + i)));
        }
        
        player.sendMessage("");
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
        
        return true;
    }
}
