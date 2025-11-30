package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nssv.skyblock.SkyblocknNSSV;
import java.util.*;

public class MinionManager {
    private final SkyblocknNSSV plugin;
    
    // Minion configuration
    private static final int MINION_ACTIVE_DELAY = 20; // seconds
    private static final int MINION_ANIM_LIMIT = 50;
    private static final boolean GLOBAL_MINION_ANIMATIONS = true;
    private static final boolean AFN_REWARDS = true;
    
    // Minion types
    private final List<String> minionList = Arrays.asList(
        "Cobblestone", "Wheat", "Deepslate", "Carrot", "Beetroot", 
        "Potato", "NetherWart", "Slayer"
    );
    
    // Upgrade costs
    private final List<Integer> upgradesCosts = Arrays.asList(
        2500, 10000, 25000, 75000, 150000, 250000, 750000, 1500000, 2500000
    );
    
    // Active minions tracking
    private final Map<Location, MinionData> activeMinions = new HashMap<>();
    private final Map<UUID, List<Location>> playerMinions = new HashMap<>();
    
    public MinionManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
        startMinionTasks();
    }
    
    private void startMinionTasks() {
        // Main minion loop - every 20 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Location, MinionData> entry : new HashMap<>(activeMinions).entrySet()) {
                    Location loc = entry.getKey();
                    MinionData data = entry.getValue();
                    
                    if (loc.getWorld() == null) continue;
                    
                    processMinionAction(loc, data);
                }
            }
        }.runTaskTimer(plugin, 400L, 400L); // 20 seconds
    }
    
    private void processMinionAction(Location loc, MinionData data) {
        String type = data.minionType;
        int level = data.level;
        
        if (type.equals("Cobblestone") || type.equals("Deepslate")) {
            processMinerMinion(loc, data);
        } else if (isFarmerMinion(type)) {
            processFarmerMinion(loc, data);
        } else if (type.equals("Slayer")) {
            processSlayerMinion(loc, data);
        }
        
        // Store items
        collectMinionOutput(loc, data);
    }
    
    private void processMinerMinion(Location loc, MinionData data) {
        // Find blocks around minion
        List<Block> nearbyBlocks = getNearbyBlocks(loc, 3);
        
        for (Block block : nearbyBlocks) {
            if (block.getType() == Material.AIR) {
                Material blockType = data.minionType.equals("Cobblestone") 
                    ? Material.COBBLESTONE 
                    : Material.DEEPSLATE;
                
                // Random chance for extra blocks (iron, gold, etc)
                if (new Random().nextInt(100) < 20) {
                    blockType = getRandomOre(data.minionType);
                }
                
                block.setType(blockType);
                playMinerSound(loc);
                
                // Add to storage
                ItemStack drop = new ItemStack(blockType);
                data.storage.add(drop);
                break;
            } else if (isMineableBlock(block.getType(), data.minionType)) {
                Material mined = block.getType();
                block.setType(Material.AIR);
                playMinerSound(loc);
                
                ItemStack drop = new ItemStack(mined);
                data.storage.add(drop);
                break;
            }
        }
    }
    
    private void processFarmerMinion(Location loc, MinionData data) {
        List<Block> nearbyBlocks = getNearbyBlocks(loc, 3);
        
        for (Block block : nearbyBlocks) {
            Block below = block.getRelative(0, -1, 0);
            
            // Plant crops
            if (block.getType() == Material.AIR && below.getType() == Material.FARMLAND) {
                Material cropType = getCropType(data.minionType);
                block.setType(cropType);
                playFarmerSound(loc, "plant");
                break;
            }
            
            // Harvest crops
            if (isFullyGrown(block, data.minionType)) {
                Material harvested = block.getType();
                block.setType(Material.AIR);
                playFarmerSound(loc, "break");
                
                ItemStack drop = getHarvestDrop(data.minionType);
                data.storage.add(drop);
                break;
            }
        }
    }
    
    private void processSlayerMinion(Location loc, MinionData data) {
        // Spawn and kill mobs
        List<LivingEntity> nearbyMobs = getNearbyMobs(loc, 5);
        
        if (nearbyMobs.size() < 3) {
            // Spawn mob
            EntityType mobType = getSlayerMobType(data.level);
            Entity mob = loc.getWorld().spawnEntity(loc.clone().add(2, 0, 0), mobType);
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (mob.isValid()) {
                        mob.remove();
                        playSlayerSound(loc);
                        
                        // Add drops
                        data.storage.add(new ItemStack(Material.ROTTEN_FLESH));
                    }
                }
            }.runTaskLater(plugin, 100L);
        }
    }
    
    private void collectMinionOutput(Location loc, MinionData data) {
        // Check if storage is full
        if (data.storage.size() >= data.storageLimit) {
            // Storage full - stop minion or send to collection
        }
    }
    
    public void placeMinion(Location loc, String type, int level, UUID owner) {
        MinionData data = new MinionData();
        data.minionType = type;
        data.level = level;
        data.owner = owner;
        data.storage = new ArrayList<>();
        data.storageLimit = 64 * (level + 1);
        
        activeMinions.put(loc, data);
        
        // Add to player's minion list
        playerMinions.putIfAbsent(owner, new ArrayList<>());
        playerMinions.get(owner).add(loc);
        
        // Spawn visual
        spawnMinionVisual(loc, data);
    }
    
    private void spawnMinionVisual(Location loc, MinionData data) {
        Location spawnLoc = loc.clone().add(0.5, 0, 0.5);
        
        ArmorStand stand = (ArmorStand) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
        stand.setCustomName("ServerSpawnMinion-" + data.minionType);
        stand.setCustomNameVisible(false);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setMarker(true);
        
        // Set head
        ItemStack head = getMinionHead(data.minionType);
        stand.getEquipment().setHelmet(head);
    }
    
    public void removeMinion(Location loc) {
        MinionData data = activeMinions.remove(loc);
        if (data != null) {
            UUID owner = data.owner;
            if (playerMinions.containsKey(owner)) {
                playerMinions.get(owner).remove(loc);
            }
            
            // Remove visual
            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
                if (entity instanceof ArmorStand) {
                    ArmorStand stand = (ArmorStand) entity;
                    if (stand.getCustomName() != null && stand.getCustomName().startsWith("ServerSpawnMinion")) {
                        entity.remove();
                    }
                }
            }
        }
    }
    
    // Helper methods
    private List<Block> getNearbyBlocks(Location center, int radius) {
        List<Block> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    blocks.add(center.clone().add(x, y, z).getBlock());
                }
            }
        }
        return blocks;
    }
    
    private List<LivingEntity> getNearbyMobs(Location center, int radius) {
        List<LivingEntity> mobs = new ArrayList<>();
        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                mobs.add((LivingEntity) entity);
            }
        }
        return mobs;
    }
    
    private boolean isFarmerMinion(String type) {
        return type.equals("Wheat") || type.equals("Carrot") || 
               type.equals("Beetroot") || type.equals("Potato") || type.equals("NetherWart");
    }
    
    private boolean isMineableBlock(Material mat, String minionType) {
        if (minionType.equals("Cobblestone")) {
            return mat == Material.COBBLESTONE || mat == Material.IRON_ORE || mat == Material.GOLD_ORE;
        }
        if (minionType.equals("Deepslate")) {
            return mat == Material.DEEPSLATE || mat.name().contains("DEEPSLATE") && mat.name().contains("ORE");
        }
        return false;
    }
    
    private Material getRandomOre(String type) {
        Random rand = new Random();
        if (type.equals("Cobblestone")) {
            return rand.nextBoolean() ? Material.IRON_ORE : Material.GOLD_ORE;
        }
        Material[] ores = {Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE, 
                          Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_EMERALD_ORE};
        return ores[rand.nextInt(ores.length)];
    }
    
    private Material getCropType(String minionType) {
        switch (minionType) {
            case "Wheat": return Material.WHEAT;
            case "Carrot": return Material.CARROTS;
            case "Beetroot": return Material.BEETROOTS;
            case "Potato": return Material.POTATOES;
            case "NetherWart": return Material.NETHER_WART;
            default: return Material.WHEAT;
        }
    }
    
    private boolean isFullyGrown(Block block, String minionType) {
        if (block.getType() == Material.WHEAT) {
            return block.getBlockData().getAsString().contains("age=7");
        }
        if (block.getType() == Material.NETHER_WART) {
            return block.getBlockData().getAsString().contains("age=3");
        }
        if (block.getType() == Material.CARROTS || block.getType() == Material.POTATOES) {
            return block.getBlockData().getAsString().contains("age=7");
        }
        if (block.getType() == Material.BEETROOTS) {
            return block.getBlockData().getAsString().contains("age=3");
        }
        return false;
    }
    
    private ItemStack getHarvestDrop(String minionType) {
        switch (minionType) {
            case "Wheat": return new ItemStack(Material.WHEAT, 2);
            case "Carrot": return new ItemStack(Material.CARROT, 3);
            case "Beetroot": return new ItemStack(Material.BEETROOT, 2);
            case "Potato": return new ItemStack(Material.POTATO, 3);
            case "NetherWart": return new ItemStack(Material.NETHER_WART, 3);
            default: return new ItemStack(Material.WHEAT);
        }
    }
    
    private EntityType getSlayerMobType(int level) {
        if (level >= 5) return EntityType.ZOMBIE;
        return EntityType.ZOMBIE;
    }
    
    private ItemStack getMinionHead(String type) {
        return new ItemStack(Material.PLAYER_HEAD);
    }
    
    private void playMinerSound(Location loc) {
        loc.getWorld().playSound(loc, Sound.BLOCK_STONE_BREAK, 1.0f, 0.9f);
    }
    
    private void playFarmerSound(Location loc, String action) {
        if (action.equals("plant")) {
            loc.getWorld().playSound(loc, Sound.ITEM_CROP_PLANT, 1.0f, 1.0f);
        } else {
            loc.getWorld().playSound(loc, Sound.BLOCK_GRASS_BREAK, 1.0f, 1.0f);
        }
    }
    
    private void playSlayerSound(Location loc) {
        loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
    }
    
    public List<Integer> getUpgradeCosts() {
        return upgradesCosts;
    }
    
    public List<String> getMinionList() {
        return minionList;
    }
    
    public MinionData getMinionData(Location loc) {
        return activeMinions.get(loc);
    }
    
    // Inner class for minion data
    public static class MinionData {
        public String minionType;
        public int level;
        public UUID owner;
        public List<ItemStack> storage;
        public int storageLimit;
        public String skin;
        public long lastAction;
    }
}
