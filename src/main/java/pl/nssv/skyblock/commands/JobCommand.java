package pl.nssv.skyblock.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.gui.JobGUI;

public class JobCommand implements CommandExecutor {
    
    private final SkyblocknNSSV plugin;
    
    public JobCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Ta komenda moze byc uzyta tylko przez gracza!");
            return true;
        }
        
        Player player = (Player) sender;
        new JobGUI(plugin, player, 0).open();
        
        return true;
    }
}
