package pl.nssv.skyblock.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.utils.ChatUtil;

public class ChatGameCommand implements CommandExecutor {
    
    private final SkyblocknNSSV plugin;
    
    public ChatGameCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("*")) {
            sender.sendMessage(ChatUtil.color("&cNie masz uprawnien!"));
            return true;
        }
        
        plugin.getChatGameManager().startGame();
        sender.sendMessage(ChatUtil.color("&aUruchomiono gre na chacie!"));
        
        return true;
    }
}
