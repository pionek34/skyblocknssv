package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nssv.skyblock.SkyblocknNSSV;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CaveManager {
    private final SkyblocknNSSV plugin;
    
    // Cave blocks
    private final Set<Material> caveBlocks = new HashSet<>();
    
    // Mine respawn system
    private final Map<Location, Long> mineRespawnQueue = new HashMap<>();
    private final Map<Long, List<Location>> mineRespawnTimes = new HashMap<>();
    private final Map<Location, MineBlockData> mineBlockData = new HashMap<>();
    
    // Lucky blocks
    private final Map<Location, UUID> luckyBlocks = new HashMap<>();
    private final Map<Location, UUID> upgradedBlocks = new HashMap<>();
    
    // Boost pads
    private final Map<Location, Location> boostPadLocations = new HashMap<>();
    
    public CaveManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
        initializeCaveBlocks();
        startRespawnTask();
        startPortalEffects();
    }
    
    private void initializeCaveBlocks() {
        caveBlocks.add(Material.COBBLESTONE);
        caveBlocks.add(Material.COAL_ORE);
        caveBlocks.add(Material.IRON_ORE);
        caveBlocks.add(Material.GOLD_ORE);
        caveBlocks.add(Material.LAPIS_ORE);
        caveBlocks.add(Material.REDSTONE_ORE);
        caveBlocks.add(Material.DIAMOND_ORE);
        caveBlocks.add(Material.EMERALD_ORE);
        caveBlocks.add(Material.DEEPSLATE_DIAMOND_ORE);
        caveBlocks.add(Material.DEEPSLATE_REDSTONE_ORE);
        caveBlocks.add(Material.DEEPSLATE_IRON_ORE);
        caveBlocks.add(Material.DEEPSLATE_LAPIS_ORE);
        caveBlocks.add(Material.DEEPSLATE_EMERALD_ORE);
    }
    
    private void startRespawnTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis() / 1000;
                List<Long> toRemove = new ArrayList<>();
                
                for (Map.Entry<Long, List<Location>> entry : mineRespawnTimes.entrySet()) {
                    long respawnTime = entry.getKey();
                    
                    if (now - respawnTime >= 10) {
                        for (Location loc : entry.getValue()) {
                            if (loc.getBlock().getType() == Material.BLACKSTONE) {
                                MineBlockData data = mineBlockData.get(loc);
                                if (data != null) {
                                    loc.getBlock().setType(data.blockType);
                                    
                                    // Lucky block chance
                                    double chance = 0.5; // Base 0.5%
                                    chance += data.extraChance;
                                    
                                    if (ThreadLocalRandom.current().nextDouble(100) < chance) {
                                        spawnLuckyBlockEffect(loc);
                                    }
                                    
                                    mineBlockData.remove(loc);
                                }
                            }
                        }
                        toRemove.add(respawnTime);
                    }
                }
                
                for (Long time : toRemove) {
                    mineRespawnTimes.remove(time);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
    
    private void startPortalEffects() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Portal effects every 3 seconds
                // TODO: Implement portal region effects
            }
        }.runTaskTimer(plugin, 60L, 60L);
    }
    
    public boolean canMineBlock(Player player, Block block) {
        if (!caveBlocks.contains(block.getType())) {
            return false;
        }
        
        int level = plugin.getDataManager().getPlayerData(player.getUniqueId()).getLevel();
        
        // Level 4+ required for deepslate ores
        if (level < 4) {
            Material type = block.getType();
            if (type == Material.DEEPSLATE_DIAMOND_ORE || type == Material.DEEPSLATE_REDSTONE_ORE ||
                type == Material.DEEPSLATE_IRON_ORE || type == Material.DEEPSLATE_LAPIS_ORE ||
                type == Material.DEEPSLATE_EMERALD_ORE) {
                player.sendMessage(ChatColor.RED + "You need Level 4 to mine this!");
                player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
                return false;
            }
        }
        
        return true;
    }
    
    public boolean processMinedBlock(Player player, Block block) {
        // Check tool
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (!isValidPickaxe(tool)) {
            player.sendMessage(ChatColor.RED + "You need a pickaxe!");
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
            return false;
        }
        
        Material blockType = block.getType();
        
        // Tool tier check
        if (tool.getType() == Material.WOODEN_PICKAXE) {
            if (blockType != Material.COAL_ORE && blockType != Material.COBBLESTONE) {
                player.sendMessage(ChatColor.RED + "Your tool is too weak!");
                player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
                return false;
            }
        }
        
        if (tool.getType() == Material.STONE_PICKAXE) {
            if (!isStonePickaxeValid(blockType)) {
                player.sendMessage(ChatColor.RED + "Your tool is too weak!");
                player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 2.0f);
                return false;
            }
        }
        
        // Give items
        List<ItemStack> drops = getBlockDrops(block, tool);
        
        for (ItemStack drop : drops) {
            if (!hasSpace(player, drop)) {
                player.sendTitle(ChatColor.RED + "Inventory Full!", 
                    ChatColor.YELLOW + "Make some space!", 10, 20, 10);
                return false;
            }
            player.getInventory().addItem(drop);
        }
        
        // Give XP
        int xp = getBlockXP(blockType);
        if (xp > 0) {
            ExperienceOrb orb = (ExperienceOrb) block.getWorld().spawnEntity(
                block.getLocation().add(0.5, 0.5, 0.5), EntityType.EXPERIENCE_ORB);
            orb.setExperience(xp);
        }
        
        // Replace with blackstone temporarily
        Location loc = block.getLocation();
        block.setType(Material.BLACKSTONE);
        
        // Queue respawn
        long respawnTime = System.currentTimeMillis() / 1000;
        mineRespawnTimes.putIfAbsent(respawnTime, new ArrayList<>());
        mineRespawnTimes.get(respawnTime).add(loc);
        
        MineBlockData data = new MineBlockData();
        data.blockType = blockType;
        data.extraChance = 0;
        mineBlockData.put(loc, data);
        
        // Check for lucky block
        if (upgradedBlocks.containsKey(loc)) {
            UUID entityId = upgradedBlocks.remove(loc);
            Entity entity = Bukkit.getEntity(entityId);
            if (entity != null) {
                entity.remove();
            }
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.0f, 1.5f);
            player.getInventory().addItem(createLuckyBlock());
        }
        
        return true;
    }
    
    private boolean isValidPickaxe(ItemStack tool) {
        if (tool == null) return false;
        Material type = tool.getType();
        return type.name().contains("PICKAXE");
    }
    
    private boolean isStonePickaxeValid(Material blockType) {
        return blockType == Material.COBBLESTONE || blockType == Material.COAL_ORE ||
               blockType == Material.IRON_ORE || blockType == Material.GOLD_ORE ||
               blockType == Material.LAPIS_ORE;
    }
    
    private List<ItemStack> getBlockDrops(Block block, ItemStack tool) {
        List<ItemStack> drops = new ArrayList<>();
        Material blockType = block.getType();
        
        // Silk Touch check
        if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            drops.add(new ItemStack(blockType));
            return drops;
        }
        
        // Process ores
        switch (blockType) {
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
                drops.add(new ItemStack(Material.COAL));
                break;
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
                drops.add(new ItemStack(Material.IRON_ORE)); // Or ingot with smelting touch
                break;
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
                drops.add(new ItemStack(Material.GOLD_ORE));
                break;
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE:
                int lapisAmount = ThreadLocalRandom.current().nextInt(4, 10);
                drops.add(new ItemStack(Material.LAPIS_LAZULI, lapisAmount));
                break;
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
                int redstoneAmount = ThreadLocalRandom.current().nextInt(4, 6);
                drops.add(new ItemStack(Material.REDSTONE, redstoneAmount));
                break;
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
                drops.add(new ItemStack(Material.DIAMOND));
                break;
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
                drops.add(new ItemStack(Material.EMERALD));
                break;
            default:
                drops.add(new ItemStack(blockType));
        }
        
        return drops;
    }
    
    private int getBlockXP(Material blockType) {
        switch (blockType) {
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
                return ThreadLocalRandom.current().nextInt(0, 3);
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE:
                return ThreadLocalRandom.current().nextInt(2, 6);
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
                return ThreadLocalRandom.current().nextInt(1, 6);
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
                return ThreadLocalRandom.current().nextInt(3, 8);
            default:
                return 0;
        }
    }
    
    private boolean hasSpace(Player player, ItemStack item) {
        return player.getInventory().firstEmpty() != -1 || 
               player.getInventory().contains(item.getType());
    }
    
    private void spawnLuckyBlockEffect(Location loc) {
        World world = loc.getWorld();
        if (world == null) return;
        
        Location spawnLoc = loc.clone().add(0, 0, -0.502);
        
        ItemDisplay display = (ItemDisplay) world.spawnEntity(spawnLoc, EntityType.ITEM_DISPLAY);
        ItemStack goldNugget = new ItemStack(Material.GOLD_NUGGET);
        display.setItemStack(goldNugget);
        
        upgradedBlocks.put(loc, display.getUniqueId());
        
        world.spawnParticle(Particle.ELECTRIC_SPARK, spawnLoc, 25, 0.4, 0.4, 0.4);
    }
    
    private ItemStack createLuckyBlock() {
        ItemStack item = new ItemStack(Material.YELLOW_STAINED_GLASS);
        // Add NBT tag for lucky block
        return item;
    }
    
    // Inner class
    private static class MineBlockData {
        Material blockType;
        double extraChance;
    }
    
    public Set<Material> getCaveBlocks() {
        return caveBlocks;
    }
    
    public Map<Location, UUID> getLuckyBlocks() {
        return luckyBlocks;
    }
}
