package pl.nssv.skyblock.commands;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;

public class CinematicCommand implements CommandExecutor, TabCompleter {
    private final SkyblocknNSSV plugin;
    
    public CinematicCommand(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("cinematic.admin")) {
            player.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        String sub = args[0].toLowerCase();
        
        switch (sub) {
            case "create":
            case "add":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Type Cinematic name!");
                    return true;
                }
                
                if (args[1].contains(" ")) {
                    player.sendMessage(ChatColor.RED + "Name can't contain spaces!");
                    return true;
                }
                
                if (plugin.getCinematicManager().createCinematic(args[1])) {
                    player.sendMessage(ChatColor.GREEN + "Success!");
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
                } else {
                    player.sendMessage(ChatColor.RED + "This Cinematic already exists!");
                }
                break;
                
            case "delete":
            case "remove":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Type Cinematic name!");
                    return true;
                }
                
                if (plugin.getCinematicManager().deleteCinematic(args[1])) {
                    player.sendMessage(ChatColor.GREEN + "Success!");
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
                } else {
                    player.sendMessage(ChatColor.RED + "This Cinematic doesn't exist!");
                }
                break;
                
            case "play":
            case "start":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Type Cinematic name!");
                    return true;
                }
                
                if (plugin.getCinematicManager().getCinematics().contains(args[1])) {
                    plugin.getCinematicManager().startCinematic(player, args[1], false);
                } else {
                    player.sendMessage(ChatColor.RED + "This Cinematic doesn't exist!");
                }
                break;
                
            case "line":
            case "lines":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Type Cinematic name!");
                    return true;
                }
                
                if (plugin.getCinematicManager().getCinematics().contains(args[1])) {
                    // Show particles for 6 seconds
                    for (int i = 0; i < 6; i++) {
                        new org.bukkit.scheduler.BukkitRunnable() {
                            @Override
                            public void run() {
                                plugin.getCinematicManager().startCinematic(player, args[1], true);
                            }
                        }.runTaskLater(plugin, i * 20L);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "This Cinematic doesn't exist!");
                }
                break;
                
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Cinematic Editor");
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "/ce " + ChatColor.YELLOW + "create [name] " + 
                          ChatColor.DARK_AQUA + "- " + ChatColor.GRAY + "Creates a new cinematic");
        player.sendMessage(ChatColor.GOLD + "/ce " + ChatColor.YELLOW + "delete [name] " + 
                          ChatColor.DARK_AQUA + "- " + ChatColor.GRAY + "Deletes existing cinematic");
        player.sendMessage(ChatColor.GOLD + "/ce " + ChatColor.YELLOW + "play [name] " + 
                          ChatColor.DARK_AQUA + "- " + ChatColor.GRAY + "Starts Cinematic");
        player.sendMessage(ChatColor.GOLD + "/ce " + ChatColor.YELLOW + "lines [name] " + 
                          ChatColor.DARK_AQUA + "- " + ChatColor.GRAY + "Shows Locs Lines");
        player.sendMessage("");
    }
    
    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        java.util.List<String> completions = new java.util.ArrayList<>();
        
        if (args.length == 1) {
            completions.addAll(java.util.Arrays.asList("create", "delete", "play", "lines"));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("play") || 
                args[0].equalsIgnoreCase("lines")) {
                completions.addAll(plugin.getCinematicManager().getCinematics());
            }
        }
        
        return completions;
    }
}
