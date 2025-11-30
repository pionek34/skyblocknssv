package pl.nssv.skyblock.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;

public class PetCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    public PetCommand(SkyblocknNSSV plugin) { this.plugin = plugin; }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        plugin.getPetManager().openPetMenu((Player) sender);
        return true;
    }
}
