package pl.nssv.skyblock.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.data.PlayerData;

public class JobListener implements Listener {
    
    private final SkyblocknNSSV plugin;
    
    public JobListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    // MINER - kopanie bloków
    @EventHandler
    public void onMine(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        
        PlayerData data = plugin.getDataManager().getPlayerData(event.getPlayer());
        if (!"Miner".equals(data.getCurrentJob())) return;
        
        Material type = event.getBlock().getType();
        
        // Rudy daja 3 exp
        if (type.name().contains("_ORE") || type.name().contains("ANCIENT_DEBRIS")) {
            plugin.getJobManager().addJobExp(event.getPlayer(), "Miner", 3);
        }
        // Kamien daje 1 exp
        else if (type == Material.STONE || type == Material.DEEPSLATE || 
                 type == Material.NETHERRACK || type == Material.END_STONE) {
            plugin.getJobManager().addJobExp(event.getPlayer(), "Miner", 1);
        }
    }
    
    // LUMBERJACK - scinanie drewna
    @EventHandler
    public void onTreeCut(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        
        PlayerData data = plugin.getDataManager().getPlayerData(event.getPlayer());
        if (!"Lumberjack".equals(data.getCurrentJob())) return;
        
        Material type = event.getBlock().getType();
        if (type.name().contains("_LOG") || type.name().contains("_WOOD")) {
            plugin.getJobManager().addJobExp(event.getPlayer(), "Lumberjack", 2);
        }
    }
    
    // BUILDER - stawianie blokow
    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        
        PlayerData data = plugin.getDataManager().getPlayerData(event.getPlayer());
        if ("Builder".equals(data.getCurrentJob())) {
            plugin.getJobManager().addJobExp(event.getPlayer(), "Builder", 1);
        }
        
        // FARMER - sadzenie roslin
        if ("Farmer".equals(data.getCurrentJob())) {
            Material type = event.getBlock().getType();
            if (type == Material.WHEAT || type == Material.CARROTS || type == Material.BEETROOTS) {
                plugin.getJobManager().addJobExp(event.getPlayer(), "Farmer", 1);
            }
        }
        
        // LUMBERJACK - sadzenie drzew
        if ("Lumberjack".equals(data.getCurrentJob())) {
            Material type = event.getBlock().getType();
            if (type.name().contains("_SAPLING")) {
                plugin.getJobManager().addJobExp(event.getPlayer(), "Lumberjack", 1);
            }
        }
    }
    
    // CRAFTER - craftowanie
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        
        if ("Crafter".equals(data.getCurrentJob())) {
            plugin.getJobManager().addJobExp(player, "Crafter", 5);
        }
    }
    
    // CRAFTER - wypalanie w piecu
    @EventHandler
    public void onSmelt(FurnaceExtractEvent event) {
        PlayerData data = plugin.getDataManager().getPlayerData(event.getPlayer());
        
        if ("Crafter".equals(data.getCurrentJob())) {
            plugin.getJobManager().addJobExp(event.getPlayer(), "Crafter", 15);
        }
    }
    
    // ENCHANTER - zaczarowanie
    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (event.isCancelled()) return;
        
        PlayerData data = plugin.getDataManager().getPlayerData(event.getEnchanter());
        
        if ("Enchanter".equals(data.getCurrentJob())) {
            plugin.getJobManager().addJobExp(event.getEnchanter(), "Enchanter", 25);
        }
    }
    
    // ALCHEMIST - warzenie mikstur
    @EventHandler
    public void onBrew(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getInventory().getType() != InventoryType.BREWING) return;
        
        Player player = (Player) event.getWhoClicked();
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        
        if ("Alchemist".equals(data.getCurrentJob())) {
            plugin.getJobManager().addJobExp(player, "Alchemist", 3);
        }
    }
    
    // FISHERMAN - lowienie ryb
    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        
        PlayerData data = plugin.getDataManager().getPlayerData(event.getPlayer());
        
        if ("Fisherman".equals(data.getCurrentJob())) {
            plugin.getJobManager().addJobExp(event.getPlayer(), "Fisherman", 5);
        }
    }
    
    // HUNTER - zabijanie mobow
    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        if (!(event.getEntity() instanceof Monster)) return;
        
        Player killer = event.getEntity().getKiller();
        PlayerData data = plugin.getDataManager().getPlayerData(killer);
        
        if ("Hunter".equals(data.getCurrentJob())) {
            plugin.getJobManager().addJobExp(killer, "Hunter", 5);
        }
    }
    
    // FARMER - rozmnazanie zwierzat
    @EventHandler
    public void onBreed(EntityBreedEvent event) {
        if (!(event.getBreeder() instanceof Player)) return;
        
        Player breeder = (Player) event.getBreeder();
        PlayerData data = plugin.getDataManager().getPlayerData(breeder);
        
        if ("Farmer".equals(data.getCurrentJob())) {
            plugin.getJobManager().addJobExp(breeder, "Farmer", 10);
        }
    }
}
