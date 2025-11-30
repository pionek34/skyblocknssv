package pl.nssv.skyblock.commands;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.nssv.skyblock.SkyblocknNSSV;
import java.util.*;

public class MinionAdminCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    
    public MinionAdminCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("minions.admin")) {
            sender.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Minions Core");
            sender.sendMessage(ChatColor.GREEN + "/minionadmin list" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "View all minions");
            sender.sendMessage(ChatColor.GREEN + "/minionadmin give [name] [tier]" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Give minion");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage(ChatColor.GREEN + "Available Minions:");
            for (String minion : plugin.getMinionManager().getMinionList()) {
                sender.sendMessage(ChatColor.YELLOW + "- " + minion);
            }
            return true;
        }
        
        if (args[0].equalsIgnoreCase("give")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players!");
                return true;
            }
            
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /minionadmin give <name> <tier>");
                return true;
            }
            
            Player player = (Player) sender;
            String minionName = args[1];
            int tier = 1;
            
            try {
                tier = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid tier!");
                return true;
            }
            
            if (!plugin.getMinionManager().getMinionList().contains(minionName)) {
                sender.sendMessage(ChatColor.RED + "Unknown minion!");
                return true;
            }
            
            ItemStack minion = createMinionItem(minionName, tier);
            player.getInventory().addItem(minion);
            sender.sendMessage(ChatColor.GREEN + "Gave " + minionName + " Minion Tier " + tier);
            
            return true;
        }
        
        return false;
    }
    
    private ItemStack createMinionItem(String name, int tier) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.GREEN + name + " Minion " + ChatColor.GRAY + "Tier " + tier);
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Place this minion on your island");
        lore.add(ChatColor.GRAY + "to automatically collect resources!");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Tier: " + tier);
        lore.add(ChatColor.YELLOW + "Type: " + name);
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
}
