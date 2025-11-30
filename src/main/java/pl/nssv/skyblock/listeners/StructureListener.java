package pl.nssv.skyblock.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import pl.nssv.skyblock.SkyblocknNSSV;

public class StructureListener implements Listener {
    private final SkyblocknNSSV plugin;
    
    public StructureListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        
        // Build Island GUI
        if (title.equals(ChatColor.WHITE + "")) {
            e.setCancelled(true);
            if (e.getClickedInventory() == player.getInventory()) return;
            
            int slot = e.getSlot();
            if (slot == 47 || slot == 48 || slot == 49 || slot == 50 || slot == 51) {
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
                player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.0f, 2.0f);
                
                plugin.getStructureManager().setJerryAsk(player.getUniqueId(), "true");
                plugin.getStructureManager().buildGenIsland(player, "GenIsland2");
            }
        }
        
        // Island Menu GUI
        String islandMenu = plugin.getConfig().getString("GUIS.IslandMenu.Default", "&8Island Menu");
        islandMenu = ChatColor.translateAlternateColorCodes('&', islandMenu);
        
        if (title.equals(islandMenu)) {
            e.setCancelled(true);
            if (e.getClickedInventory() == player.getInventory()) return;
            
            int slot = e.getSlot();
            
            if (slot == 37 || slot == 38 || slot == 39) {
                player.closeInventory();
                String hasIsland = plugin.getPlaceholderAPI().setPlaceholders(player, "%bskyblock_has_island%");
                
                if (hasIsland.equals("false")) {
                    Bukkit.dispatchCommand(player, "is create");
                } else {
                    Bukkit.dispatchCommand(player, "is go");
                }
            }
            
            if (slot == 1) {
                // Upgrades
                player.closeInventory();
            }
            
            if (slot == 2) {
                Bukkit.dispatchCommand(player, "is border");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
            }
            
            if (slot == 10) {
                plugin.getBankManager().openIslandBank(player, 1);
            }
        }
        
        // Bank GUI - Withdraw
        String bankWithdraw = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("GUIS.BankWithdraw.Default", "&cWithdraw"));
        
        if (title.equals(bankWithdraw)) {
            e.setCancelled(true);
            
            int slot = e.getSlot();
            
            if (slot == 19 || slot == 20 || slot == 21) {
                plugin.getBankManager().openIslandBank(player, 1);
                return;
            }
            
            // Withdraw percentages
            int[] slots = {1, 3, 5, 7};
            int[] percentages = {25, 50, 75, 100};
            
            for (int i = 0; i < slots.length; i++) {
                if (slot == slots[i]) {
                    String balStr = plugin.getPlaceholderAPI().setPlaceholders(player, 
                        "%bskyblock_island_balance%");
                    
                    try {
                        double balance = Double.parseDouble(balStr);
                        double amount = (balance / 100) * percentages[i];
                        
                        if (amount < 0.005) {
                            player.sendMessage(ChatColor.RED + "Amount too small!");
                            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
                        } else {
                            Bukkit.dispatchCommand(player, "is bank withdraw " + amount);
                        }
                    } catch (NumberFormatException ex) {
                        player.sendMessage(ChatColor.RED + "Error reading balance!");
                    }
                    return;
                }
            }
            
            if (slot == 23 || slot == 24 || slot == 25) {
                plugin.getBankManager().customWithdraw(player);
            }
        }
        
        // Bank GUI - Deposit
        String bankDeposit = ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("GUIS.BankDeposit.Default", "&aDeposit"));
        
        if (title.equals(bankDeposit)) {
            e.setCancelled(true);
            
            int slot = e.getSlot();
            
            if (slot == 19 || slot == 20 || slot == 21) {
                plugin.getBankManager().openIslandBank(player, 1);
                return;
            }
            
            // Deposit percentages
            int[] slots = {1, 3, 5, 7};
            int[] percentages = {25, 50, 75, 100};
            
            for (int i = 0; i < slots.length; i++) {
                if (slot == slots[i]) {
                    double balance = plugin.getEconomy().getBalance(player);
                    double amount = Math.floor((balance / 100) * percentages[i]);
                    
                    if (amount < 0.005) {
                        player.sendMessage(ChatColor.RED + "Amount too small!");
                        player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
                    } else {
                        Bukkit.dispatchCommand(player, "is bank deposit " + amount);
                    }
                    return;
                }
            }
            
            if (slot == 23 || slot == 24 || slot == 25) {
                plugin.getBankManager().customDeposit(player);
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        String objective = plugin.getObjectiveManager().getObjective(player);
        
        if (objective.equals("Collect4Logs") || objective.contains("Mine")) {
            plugin.getObjectiveManager().questCheck(player, e.getBlock().getType());
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        String objective = plugin.getObjectiveManager().getObjective(player);
        
        if (objective.equals("TalkWithJerry1") || objective.equals("Wait1")) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Complete your current objective first!");
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
            return;
        }
        
        if (objective.contains("BuildOverIsland") || objective.contains("ExpandIsland")) {
            plugin.getObjectiveManager().handlePlaceBlock(player);
        }
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String action = plugin.getBankManager().getBankAction(player.getUniqueId());
        
        if (action != null && (action.equals("Deposit") || action.equals("Withdraw"))) {
            e.setCancelled(true);
            
            String message = e.getMessage();
            
            new org.bukkit.scheduler.BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(player, "is bank " + action.toLowerCase() + " " + message);
                    player.sendTitle("", "", 0, 1, 0);
                    plugin.getBankManager().removeBankAction(player.getUniqueId());
                }
            }.runTask(plugin);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        plugin.getBankManager().removeBankAction(player.getUniqueId());
        plugin.getObjectiveManager().handleJoin(player);
    }
    
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        String objective = plugin.getObjectiveManager().getObjective(player);
        
        if (player.getWorld().getName().equals("world") && objective.equals("EnterPortal")) {
            Location spawn = plugin.getServer().getWorlds().get(0).getSpawnLocation();
            player.teleport(spawn);
            
            int level = plugin.getDataManager().getPlayerData(player.getUniqueId()).getLevel();
            if (level == 0) {
                plugin.getDataManager().getPlayerData(player.getUniqueId()).setLevel(1);
            }
            
            plugin.getObjectiveManager().newObjective(player, "TalkWithJerry2");
        }
    }
    
    @EventHandler
    public void onPortalEnter(PlayerPortalEvent e) {
        Player player = e.getPlayer();
        
        if (!player.getWorld().getName().equals("world")) {
            String objective = plugin.getObjectiveManager().getObjective(player);
            
            // Check if valid portal
            boolean validPortal = false;
            Location loc = player.getLocation();
            
            for (int y = 0; y < 5; y++) {
                Block below = loc.clone().subtract(0, y, 0).getBlock();
                if (below.getType() == Material.OBSIDIAN) {
                    validPortal = true;
                    break;
                }
            }
            
            if (!validPortal) {
                if (objective.contains("Wait") || objective.contains("TalkWithJerry1") || 
                    objective.contains("Collect4Logs") || objective.contains("BuildOverIsland")) {
                    
                    player.sendMessage(ChatColor.RED + "Complete basic tasks first!");
                    player.setVelocity(player.getLocation().getDirection().multiply(-1).setY(0.3));
                    player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
                    
                    new org.bukkit.scheduler.BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.dispatchCommand(player, "is go");
                        }
                    }.runTaskLater(plugin, 20L);
                    
                    e.setCancelled(true);
                } else {
                    // Allow teleport
                    e.setCancelled(true);
                    
                    new org.bukkit.scheduler.BukkitRunnable() {
                        @Override
                        public void run() {
                            Location spawn = plugin.getServer().getWorlds().get(0).getSpawnLocation();
                            player.teleport(spawn);
                        }
                    }.runTaskLater(plugin, 10L);
                }
            } else {
                e.setCancelled(true);
            }
        }
    }
}
