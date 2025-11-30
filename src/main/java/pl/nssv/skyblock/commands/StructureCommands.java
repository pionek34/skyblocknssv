package pl.nssv.skyblock.commands;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;

public class StructureCommands implements CommandExecutor, TabCompleter {
    private final SkyblocknNSSV plugin;
    
    public StructureCommands(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        // /detectislandcreate
        if (cmd.getName().equalsIgnoreCase("detectislandcreate")) {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(ChatColor.RED + "Command only executable by console!");
                if (sender instanceof Player) {
                    ((Player)sender).playSound(((Player)sender).getLocation(), 
                        Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
                }
                return true;
            }
            
            if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    plugin.getStructureManager().onIslandCreate(target);
                }
            }
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // /genisland
        if (cmd.getName().equalsIgnoreCase("genisland")) {
            String owner = plugin.getPlaceholderAPI().setPlaceholders(player, "%bskyblock_island_owner%");
            
            if (!owner.equals(player.getName())) {
                player.sendMessage(ChatColor.RED + "Only island leader can do this!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.15f);
                return true;
            }
            
            String jerryAsk = plugin.getStructureManager().getJerryAsk(player.getUniqueId());
            
            if ("Not created".equals(jerryAsk)) {
                plugin.getStructureManager().setJerryAsk(player.getUniqueId(), 
                    String.valueOf(System.currentTimeMillis()));
                plugin.getObjectiveManager().newObjective(player, "Wait1");
                
                // Jerry dialog
                player.sendMessage(ChatColor.GREEN + "Jerry: Hello " + player.getName() + "!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.15f);
                
                new org.bukkit.scheduler.BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.GREEN + "Jerry: I will help you build your island!");
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.15f);
                        
                        new org.bukkit.scheduler.BukkitRunnable() {
                            @Override
                            public void run() {
                                plugin.getStructureManager().buildIsland(player);
                                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
                            }
                        }.runTaskLater(plugin, 30L);
                    }
                }.runTaskLater(plugin, 30L);
            } else if ("true".equals(jerryAsk)) {
                player.sendMessage(ChatColor.RED + "Jerry: Please wait, I'm already working!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_TRADE, 1.0f, 1.15f);
            } else {
                try {
                    long timestamp = Long.parseLong(jerryAsk);
                    long diff = System.currentTimeMillis() - timestamp;
                    
                    if (diff > 10000) { // 10 seconds
                        plugin.getStructureManager().buildIsland(player);
                    }
                } catch (NumberFormatException e) {
                    plugin.getStructureManager().buildIsland(player);
                }
            }
            return true;
        }
        
        // /setwarp
        if (cmd.getName().equalsIgnoreCase("setwarp")) {
            if (!player.hasPermission("structure.setwarp")) {
                player.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /setwarp <name>");
                return true;
            }
            
            String warpName = args[0];
            plugin.getWarpManager().setWarp(warpName, player.getLocation());
            player.sendMessage(ChatColor.GREEN + "Warp " + ChatColor.GOLD + warpName + ChatColor.GREEN + " created!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
            return true;
        }
        
        // /delwarp
        if (cmd.getName().equalsIgnoreCase("delwarp")) {
            if (!player.hasPermission("structure.delwarp")) {
                player.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /delwarp <name>");
                return true;
            }
            
            String warpName = args[0];
            if (plugin.getWarpManager().warpExists(warpName)) {
                plugin.getWarpManager().deleteWarp(warpName);
                player.sendMessage(ChatColor.GREEN + "Warp " + ChatColor.GOLD + warpName + ChatColor.GREEN + " deleted!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
            } else {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
            }
            return true;
        }
        
        // /warp and /warps
        if (cmd.getName().equalsIgnoreCase("warp") || cmd.getName().equalsIgnoreCase("warps")) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /warp <name>");
                player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
                return true;
            }
            
            String warpName = args[0];
            plugin.getWarpManager().warpTo(player, warpName);
            return true;
        }
        
        // /bank
        if (cmd.getName().equalsIgnoreCase("bank") || cmd.getName().equalsIgnoreCase("isbank")) {
            plugin.getBankManager().openIslandBank(player, 1);
            return true;
        }
        
        // /help and /panel
        if (cmd.getName().equalsIgnoreCase("help") || cmd.getName().equalsIgnoreCase("panel") ||
            cmd.getName().equalsIgnoreCase("islandpanel") || cmd.getName().equalsIgnoreCase("ispanel")) {
            
            String title = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("GUIS.IslandMenu.Default", "&8Island Menu"));
            
            org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, 45, title);
            
            // Fill with items from config
            org.bukkit.inventory.ItemStack teleport = new org.bukkit.inventory.ItemStack(Material.ENDER_PEARL);
            org.bukkit.inventory.meta.ItemMeta meta = teleport.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Teleport to Island");
            teleport.setItemMeta(meta);
            
            inv.setItem(37, teleport);
            inv.setItem(38, teleport);
            inv.setItem(39, teleport);
            
            player.openInventory(inv);
            return true;
        }
        
        return false;
    }
    
    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("warp") || cmd.getName().equalsIgnoreCase("warps")) {
            if (args.length == 1) {
                return new java.util.ArrayList<>(plugin.getWarpManager().getWarpNames());
            }
        }
        return null;
    }
}
