package pl.nssv.skyblock.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.utils.ChatUtil;

import java.util.Arrays;
import java.util.List;

public class CommandListener implements Listener {
    
    private final SkyblocknNSSV plugin;
    private final List<String> blockedCommands = Arrays.asList(
        "minecraft:help", "minecraft:tell", "minecraft:me", "?", "bukkit:?", 
        "pl", "plugins", "plugin", "bukkit:help", "icanhasbukkit", 
        "bukkit:version", "bukkit:ver", "version", "ver", "about", 
        "bukkit:about", "bukkit:pl", "bukkit:plugins"
    );
    
    public CommandListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase().substring(1).split(" ")[0];
        
        if (blockedCommands.contains(command)) {
            if (!event.getPlayer().hasPermission("*")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatUtil.color(plugin.getConfig().getString("Utils.CantUseCommand")));
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 2.0f);
            }
        }
    }
    
    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent event) {
        if (!event.getPlayer().hasPermission("*")) {
            // Ukryj wszystkie komendy dla graczy bez *
            event.getCommands().clear();
            
            // Dodaj tylko dozwolone komendy
            List<String> allowedCommands = plugin.getConfig().getStringList("Utils.CommandList");
            event.getCommands().addAll(allowedCommands);
        }
    }
}
