package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.nssv.skyblock.SkyblocknNSSV;
import java.util.*;

public class BankManager {
    private final SkyblocknNSSV plugin;
    private final Map<String, Boolean> bankInUse = new HashMap<>();
    private final Map<UUID, Integer> bankPage = new HashMap<>();
    private final Map<UUID, Long> bankPageCooldown = new HashMap<>();
    private final Map<String, List<ItemStack>> bankItems = new HashMap<>();
    private final Map<UUID, String> bankAction = new HashMap<>();
    
    public BankManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    public void openIslandBank(Player player, int page) {
        if (plugin.getDataManager().getPlayerData(player.getUniqueId()).getLevel() < 5) {
            player.sendMessage(ChatColor.RED + "Requires level 5!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.15f);
            return;
        }
        
        if (!player.getWorld().getName().equals("bskyblock_world")) {
            player.sendMessage(ChatColor.RED + "Wrong world!");
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
            return;
        }
        
        // Cooldown check
        long now = System.currentTimeMillis();
        if (bankPageCooldown.containsKey(player.getUniqueId())) {
            long last = bankPageCooldown.get(player.getUniqueId());
            if (now - last < 100) return;
        }
        bankPageCooldown.put(player.getUniqueId(), now);
        
        String owner = plugin.getPlaceholderAPI().setPlaceholders(player, "%bskyblock_island_owner%");
        
        if (bankInUse.getOrDefault(owner, false)) {
            player.sendMessage(ChatColor.RED + "Bank is in use by someone else!");
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
            return;
        }
        
        bankInUse.put(owner, true);
        bankPage.put(player.getUniqueId(), page);
        
        String title = ChatColor.DARK_GRAY + "Island Bank - Page " + page;
        Inventory inv = Bukkit.createInventory(null, 54, title);
        
        // Bank info head
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Island Bank");
        
        String balance = plugin.getPlaceholderAPI().setPlaceholders(player, "%bskyblock_island_balance%");
        int itemCount = bankItems.getOrDefault(owner, new ArrayList<>()).size();
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Balance: " + ChatColor.GREEN + balance);
        lore.add(ChatColor.GRAY + "Items: " + ChatColor.YELLOW + itemCount);
        meta.setLore(lore);
        head.setItemMeta(meta);
        
        inv.setItem(1, head);
        
        // Page buttons
        if (page > 1) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.setDisplayName(ChatColor.YELLOW + "Previous Page");
            prev.setItemMeta(prevMeta);
            inv.setItem(45, prev);
        }
        
        if (page < 4) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
            next.setItemMeta(nextMeta);
            inv.setItem(53, next);
        }
        
        // Deposit/Withdraw buttons
        ItemStack deposit = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta depositMeta = deposit.getItemMeta();
        depositMeta.setDisplayName(ChatColor.GREEN + "Deposit");
        deposit.setItemMeta(depositMeta);
        inv.setItem(19, deposit);
        inv.setItem(20, deposit);
        inv.setItem(21, deposit);
        
        ItemStack withdraw = new ItemStack(Material.RED_CONCRETE);
        ItemMeta withdrawMeta = withdraw.getItemMeta();
        withdrawMeta.setDisplayName(ChatColor.RED + "Withdraw");
        withdraw.setItemMeta(withdrawMeta);
        inv.setItem(37, withdraw);
        inv.setItem(38, withdraw);
        inv.setItem(39, withdraw);
        
        player.openInventory(inv);
    }
    
    public void customDeposit(Player player) {
        bankAction.put(player.getUniqueId(), "Deposit");
        player.closeInventory();
        player.sendTitle(
            ChatColor.GOLD + "Enter amount",
            ChatColor.YELLOW + "Type in chat",
            10, 400, 10
        );
        
        new org.bukkit.scheduler.BukkitRunnable() {
            int time = 0;
            
            @Override
            public void run() {
                time++;
                if (!bankAction.containsKey(player.getUniqueId()) || time >= 20) {
                    player.sendTitle("", "", 0, 1, 0);
                    player.sendMessage(ChatColor.RED + "Time expired!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
                    bankAction.remove(player.getUniqueId());
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    
    public void customWithdraw(Player player) {
        bankAction.put(player.getUniqueId(), "Withdraw");
        player.closeInventory();
        player.sendTitle(
            ChatColor.GOLD + "Enter amount",
            ChatColor.YELLOW + "Type in chat",
            10, 400, 10
        );
        
        new org.bukkit.scheduler.BukkitRunnable() {
            int time = 0;
            
            @Override
            public void run() {
                time++;
                if (!bankAction.containsKey(player.getUniqueId()) || time >= 20) {
                    player.sendTitle("", "", 0, 1, 0);
                    player.sendMessage(ChatColor.RED + "Time expired!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
                    bankAction.remove(player.getUniqueId());
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    
    public String getBankAction(UUID uuid) {
        return bankAction.get(uuid);
    }
    
    public void removeBankAction(UUID uuid) {
        bankAction.remove(uuid);
    }
    
    public void releaseBankLock(String owner) {
        bankInUse.remove(owner);
    }
    
    public Integer getBankPage(UUID uuid) {
        return bankPage.get(uuid);
    }
}
