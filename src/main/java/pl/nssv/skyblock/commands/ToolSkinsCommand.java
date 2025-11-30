package pl.nssv.skyblock.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;

public class ToolSkinsCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    public ToolSkinsCommand(SkyblocknNSSV plugin) { this.plugin = plugin; }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        plugin.getToolSkinManager().openToolSkinsGUI((Player) sender);
        return true;
    }
}
