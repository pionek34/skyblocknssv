package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;
import pl.nssv.skyblock.SkyblocknNSSV;

import java.util.*;

public class StructureManager {
    private final SkyblocknNSSV plugin;
    
    // Options z Skript
    private static final int BUILD_TIME = 7;
    private static final int X_OFFSET = -37;
    private static final int Y_OFFSET = 1;
    private static final int Z_OFFSET = -2;
    private static final int NPC_OFFSET_X = -9;
    private static final int NPC_OFFSET_Y = 14;
    private static final int NPC_OFFSET_Z = -1;
    
    // Structure coordinates (przykładowe - do zmiany na właściwe)
    private static final Location STRUCTURE_LOC1 = new Location(null, -38, 87, 25);
    private static final Location STRUCTURE_CENTER = new Location(null, -48, 69, 34);
    private static final Location STRUCTURE_LOC2 = new Location(null, -58, 67, 43);
    
    private final Map<UUID, String> jerryAsk = new HashMap<>();
    private final Map<UUID, Integer> jerryID = new HashMap<>();
    private final Map<UUID, Display> effect4List = new HashMap<>();
    private final Set<UUID> inputBlockEntities = new HashSet<>();
    
    public StructureManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    // buildisland() function
    public void buildIsland(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.WHITE + "");
        
        ItemStack button = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta meta = button.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Confirm");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to build island");
        meta.setLore(lore);
        button.setItemMeta(meta);
        
        inv.setItem(47, button);
        inv.setItem(48, button);
        inv.setItem(49, button);
        inv.setItem(50, button);
        inv.setItem(51, button);
        
        player.openInventory(inv);
    }
    
    // onislandcreate() function  
    public void onIslandCreate(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                String owner = plugin.getPlaceholderAPI().setPlaceholders(player, "%bskyblock_island_owner%");
                
                if (!owner.equals(player.getName())) {
                    plugin.getObjectiveManager().newObjective(player, "End");
                    return;
                }
                
                plugin.getObjectiveManager().newObjective(player, "TalkWithJerry1");
                
                // Set island range
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bsbadmin range set " + player.getName() + " 75");
                
                Location centerPos = centerBlock(player);
                Location npcPos = centerPos.clone();
                npcPos.add(NPC_OFFSET_X, NPC_OFFSET_Y, NPC_OFFSET_Z);
                
                // Create Jerry NPC (Citizens)
                String npcName = player.getName() + "-Jerry";
                
                // Cloud particles
                player.getWorld().spawnParticle(Particle.CLOUD, npcPos, 20, 0.2, 0.2, 0.2, 0);
                
                // Store Jerry data
                jerryAsk.put(player.getUniqueId(), "Not created");
                
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Citizens NPC creation would go here
                        // execute console command "npc gravity"
                    }
                }.runTaskLater(plugin, 26L); // 1.3 seconds
            }
        }.runTaskLater(plugin, 20L); // 1 second
    }
    
    // centerBlock() function
    public Location centerBlock(Player player) {
        String xStr = plugin.getPlaceholderAPI().setPlaceholders(player, "%bskyblock_island_center_x%");
        String yStr = plugin.getPlaceholderAPI().setPlaceholders(player, "%bskyblock_island_center_y%");
        String zStr = plugin.getPlaceholderAPI().setPlaceholders(player, "%bskyblock_island_center_z%");
        
        double x = Double.parseDouble(xStr) + 0.5;
        double y = Double.parseDouble(yStr) + 0.5;
        double z = Double.parseDouble(zStr) + 0.5;
        
        Location pos = new Location(Bukkit.getWorld("bskyblock_world"), x, y, z);
        
        // Find bedrock in radius 2
        for (Block block : getNearbyBlocks(pos, 2)) {
            if (block.getType() == Material.BEDROCK) {
                return block.getLocation().add(0.5, 0, 0.5);
            }
        }
        
        return pos;
    }
    
    // portalEffect() function
    public void portalEffect(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                String objective = plugin.getObjectiveManager().getObjective(player);
                
                if (!objective.equals("BuildOverIsland") && !objective.equals("EnterPortal")) {
                    cancel();
                    return;
                }
                
                if (!player.isOnline() || !player.getWorld().getName().equals("bskyblock_world")) {
                    cancel();
                    return;
                }
                
                Location loc = centerBlock(player);
                loc.add(14.5, -41, -1);
                
                effect4(loc, "8336c5", "", 1, 0.1, false, 0, null, 0);
            }
        }.runTaskTimer(plugin, 0L, 10L); // Every 0.5 seconds
    }
    
    // effect4() function - Display Entity effect
    public void effect4(Location loc, String color, String unicode, double size, double speed, 
                       boolean rotate, double extrar, Entity follow, double ab) {
        
        Location spawnLoc = loc.clone();
        spawnLoc.setYaw(spawnLoc.getYaw() - 90);
        spawnLoc.setPitch(spawnLoc.getPitch() + (float)extrar);
        
        TextDisplay display = (TextDisplay) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.TEXT_DISPLAY);
        display.setCustomName("Effect4");
        
        if (rotate) {
            display.setBillboard(Display.Billboard.CENTER);
        }
        
        display.setBrightness(new Display.Brightness(10, 10));
        display.setShadowed(false);
        
        Transformation trans = display.getTransformation();
        trans.getScale().set(0, 0, 0);
        display.setTransformation(trans);
        
        display.setInterpolationDelay(0);
        display.setInterpolationDuration(0);
        display.setTeleportDuration(1);
        
        display.setTextOpacity((byte) 255);
        display.setText("<#" + color + ">" + unicode);
        display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        
        effect4List.put(display.getUniqueId(), display);
        
        if (follow != null) {
            display.setVisibleByDefault(false);
        }
        
        // Animate
        new BukkitRunnable() {
            int brightness = 255;
            int tick = 0;
            
            @Override
            public void run() {
                if (!display.isValid()) {
                    cancel();
                    return;
                }
                
                // Scale up
                if (tick < 20) {
                    entitySize(display, size);
                }
                
                // Fade out
                brightness -= 15;
                if (brightness < 6) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            display.remove();
                            effect4List.remove(display.getUniqueId());
                        }
                    }.runTaskLater(plugin, 20L);
                    cancel();
                    return;
                }
                
                display.setTextOpacity((byte) brightness);
                
                if (follow != null) {
                    Location newLoc = follow.getLocation().add(0, ab, 0);
                    display.teleport(newLoc);
                }
                
                tick++;
            }
        }.runTaskTimer(plugin, 2L, 1L);
    }
    
    // entitySize() function
    private void entitySize(Display entity, double targetSize) {
        new BukkitRunnable() {
            double currentSize = 0;
            int tick = 0;
            
            @Override
            public void run() {
                if (tick >= 20) {
                    cancel();
                    return;
                }
                
                currentSize += targetSize / 20.0;
                
                Transformation trans = entity.getTransformation();
                trans.getScale().set((float)currentSize, (float)currentSize, (float)currentSize);
                entity.setTransformation(trans);
                entity.setInterpolationDelay(0);
                entity.setInterpolationDuration(1);
                
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    // buildgenisland() function
    public void buildGenIsland(Player player, String type) {
        Location targetPos = centerBlock(player);
        targetPos.add(X_OFFSET, Y_OFFSET, Z_OFFSET);
        
        World sourceWorld = Bukkit.getWorld("world");
        if (sourceWorld == null) return;
        
        List<Block> blocks = new ArrayList<>();
        List<Location> targetLocs = new ArrayList<>();
        List<Location> fireworkLocs = new ArrayList<>();
        List<Location> portalBlocks = new ArrayList<>();
        
        // Copy structure
        Location loc1 = STRUCTURE_LOC1.clone();
        Location loc2 = STRUCTURE_LOC2.clone();
        loc1.setWorld(sourceWorld);
        loc2.setWorld(sourceWorld);
        
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = sourceWorld.getBlockAt(x, y, z);
                    if (block.getType() != Material.AIR) {
                        Location targetLoc = targetPos.clone();
                        targetLoc.add(
                            x - STRUCTURE_CENTER.getBlockX(),
                            y - STRUCTURE_CENTER.getBlockY() + 1,
                            z - STRUCTURE_CENTER.getBlockZ()
                        );
                        
                        if (block.getType() == Material.COBWEB) {
                            fireworkLocs.add(targetLoc);
                            continue;
                        }
                        
                        if (block.getType() == Material.PURPLE_STAINED_GLASS) {
                            portalBlocks.add(targetLoc);
                            continue;
                        }
                        
                        blocks.add(block);
                        targetLocs.add(targetLoc);
                    }
                }
            }
        }
        
        // Build animation
        double pitchIncrement = 1.5 / blocks.size();
        
        new BukkitRunnable() {
            int index = 0;
            int buildTick = 0;
            
            @Override
            public void run() {
                if (index >= blocks.size()) {
                    // Build portals
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Location loc : portalBlocks) {
                                loc.getBlock().setType(Material.NETHER_PORTAL);
                                loc.getWorld().spawnParticle(Particle.PORTAL, loc, 20, 0.2, 0.2, 0.2, 0);
                            }
                            
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
                            
                            // Fireworks
                            for (Location loc : fireworkLocs) {
                                FireworkEffect effect = FireworkEffect.builder()
                                    .with(FireworkEffect.Type.BALL_LARGE)
                                    .withColor(Color.LIME, Color.ORANGE)
                                    .withFade(Color.RED)
                                    .flicker(true)
                                    .trail(true)
                                    .build();
                                
                                Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
                                fw.getFireworkMeta().addEffect(effect);
                                fw.getFireworkMeta().setPower(1);
                                fw.setFireworkMeta(fw.getFireworkMeta());
                            }
                            
                            jerryRespond(player);
                        }
                    }.runTaskLater(plugin, 60L);
                    
                    cancel();
                    return;
                }
                
                Block sourceBlock = blocks.get(index);
                Location targetLoc = targetLocs.get(index);
                
                inputBlock(targetLoc, sourceBlock, pitchIncrement, index, player);
                
                buildTick++;
                if (buildTick >= BUILD_TIME) {
                    buildTick = 0;
                } else {
                    index--;
                }
                
                index++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    // inputblock() function - Animated block placement
    private void inputBlock(Location location, Block sourceBlock, double pitch, int loopCounter, Player player) {
        if (!player.isOnline() || !player.getWorld().getName().equals("bskyblock_world")) {
            location.getBlock().setType(sourceBlock.getType());
            return;
        }
        
        Location spawnLoc = location.clone();
        Random random = new Random();
        spawnLoc.add(
            random.nextInt(51) - 25,
            random.nextInt(51) - 25,
            random.nextInt(51) - 25
        );
        
        ItemDisplay itemDisplay = (ItemDisplay) location.getWorld().spawnEntity(spawnLoc, EntityType.ITEM_DISPLAY);
        itemDisplay.setCustomName("InputBlock");
        itemDisplay.setItemStack(new ItemStack(sourceBlock.getType()));
        itemDisplay.setTeleportDuration(50);
        itemDisplay.setInterpolationDelay(0);
        itemDisplay.setInterpolationDuration(50);
        
        Transformation trans = itemDisplay.getTransformation();
        trans.getScale().set(0, 0, 0);
        itemDisplay.setTransformation(trans);
        
        inputBlockEntities.add(itemDisplay.getUniqueId());
        
        new BukkitRunnable() {
            @Override
            public void run() {
                Transformation t = itemDisplay.getTransformation();
                t.getScale().set(0.7f, 0.7f, 0.7f);
                itemDisplay.setTransformation(t);
                
                itemDisplay.teleport(location);
                
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        itemDisplay.remove();
                        inputBlockEntities.remove(itemDisplay.getUniqueId());
                        location.getBlock().setType(sourceBlock.getType());
                        
                        float soundPitch = (float)(0.5 + (pitch * loopCounter));
                        player.playSound(player.getLocation(), Sound.BLOCK_WOOD_BREAK, 0.2f, soundPitch);
                    }
                }.runTaskLater(plugin, 52L); // 2.6 seconds
            }
        }.runTaskLater(plugin, 2L);
    }
    
    // jerryrespond() function
    private void jerryRespond(Player player) {
        // Send messages and update objective
        player.sendMessage(ChatColor.GREEN + "Jerry: Island construction complete!");
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.15f);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getObjectiveManager().newObjective(player, "Collect4Logs");
                
                Location npcPos = centerBlock(player);
                npcPos.add(NPC_OFFSET_X, NPC_OFFSET_Y, NPC_OFFSET_Z);
                
                player.getWorld().spawnParticle(Particle.CLOUD, npcPos, 20, 0.2, 0.2, 0.2, 0);
                
                // Remove Jerry NPC
                jerryID.remove(player.getUniqueId());
                jerryAsk.remove(player.getUniqueId());
            }
        }.runTaskLater(plugin, 26L);
    }
    
    // Helper method
    private List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    blocks.add(location.clone().add(x, y, z).getBlock());
                }
            }
        }
        return blocks;
    }
    
    public String getJerryAsk(UUID uuid) {
        return jerryAsk.get(uuid);
    }
    
    public void setJerryAsk(UUID uuid, String value) {
        jerryAsk.put(uuid, value);
    }
    
    public void cleanup() {
        // Cleanup all display entities
        for (Display display : effect4List.values()) {
            if (display.isValid()) {
                display.remove();
            }
        }
        effect4List.clear();
    }
}
