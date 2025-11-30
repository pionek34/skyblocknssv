package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nssv.skyblock.SkyblocknNSSV;

public class GlobalFlyManager {
    private final SkyblocknNSSV plugin;
    
    private int globalFlyCash = 0;
    private Long globalFlyStartTime = null;
    private Location globalFlyLocation = null;

    public GlobalFlyManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
        
        // Load location from config
        if (plugin.getConfig().contains("GlobalFly.Location")) {
            try {
                World world = Bukkit.getWorld(plugin.getConfig().getString("GlobalFly.Location.world"));
                double x = plugin.getConfig().getDouble("GlobalFly.Location.x");
                double y = plugin.getConfig().getDouble("GlobalFly.Location.y");
                double z = plugin.getConfig().getDouble("GlobalFly.Location.z");
                globalFlyLocation = new Location(world, x, y, z);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load GlobalFly location!");
            }
        }
        
        // Start auto-check for timeout
        startAutoChecker();
    }

    public void depositMoney(Player player, int amount) {
        globalFlyCash += amount;
        
        // Play effects
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
        
        if (globalFlyLocation != null) {
            globalFlyLocation.getWorld().spawnParticle(
                Particle.FIREWORK,
                globalFlyLocation,
                20,
                1, 1, 1,
                0.1
            );
        }
        
        String message = plugin.getConfig().getString("Utils3.SuccessDeposit", "&aDeposited money!");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        
        // Play sound sequence
        new BukkitRunnable() {
            int count = 0;
            
            @Override
            public void run() {
                if (count >= 3) {
                    cancel();
                    return;
                }
                
                float pitch = 1.0f + (count * 0.2f);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, pitch);
                count++;
            }
        }.runTaskTimer(plugin, 0L, 4L);
        
        // Check if reached target
        checkGlobalFly();
    }

    private void checkGlobalFly() {
        int cost = plugin.getConfig().getInt("Utils3.GlobalFlyCost", 100000);
        
        if (globalFlyCash >= cost) {
            globalFlyCash = 0;
            startGlobalFly();
            
            // Update hologram to 100%
            updateHologram(100);
        } else {
            // Update hologram percentage
            int percentage = (globalFlyCash * 100) / cost;
            updateHologram(percentage);
        }
    }

    private void startGlobalFly() {
        globalFlyStartTime = System.currentTimeMillis();
        
        // Broadcast messages
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("");
            String title = plugin.getConfig().getString("Utils3.GlobalFlyTitle", "&6&lGLOBAL FLY");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', title));
            player.sendMessage("");
            
            String line1 = plugin.getConfig().getString("Utils3.GlobalFlyStart.1", "&aGlobal fly activated!");
            String line2 = plugin.getConfig().getString("Utils3.GlobalFlyStart.2", "&aDuration: 30 minutes");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line1));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line2));
            player.sendMessage("");
            
            String line3 = plugin.getConfig().getString("Utils3.GlobalFlyStart.3", "&7Everyone can fly now!");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line3));
            player.sendMessage("");
            
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.75f);
        }
        
        // Firework effects
        if (globalFlyLocation != null) {
            spawnFireworks();
        }
        
        // Update hologram
        String enabled = plugin.getConfig().getString("Utils3.GlobalFlyEnabled", "&aACTIVE");
        // execute console command "dh line set ServerFly 1 10 %enabled%"
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dh line set ServerFly 1 10 " + enabled);
    }

    private void spawnFireworks() {
        if (globalFlyLocation == null) return;
        
        new BukkitRunnable() {
            int count = 0;
            Color[] colors = {
                Color.RED, Color.ORANGE,
                Color.YELLOW, Color.LIME,
                Color.AQUA, Color.BLUE
            };
            
            @Override
            public void run() {
                if (count >= 5) {
                    cancel();
                    return;
                }
                
                // Spawn firework
                globalFlyLocation.getWorld().spawnParticle(
                    Particle.FIREWORK,
                    globalFlyLocation,
                    50,
                    2, 2, 2,
                    0.2
                );
                
                // Rank effect (particles)
                for (int i = 0; i < 8; i++) {
                    spawnRankEffect();
                }
                
                count++;
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    private void spawnRankEffect() {
        if (globalFlyLocation == null) return;
        
        Location start = globalFlyLocation.clone();
        start.add(
            (Math.random() - 0.5) * 50,
            (Math.random() - 0.5) * 50,
            (Math.random() - 0.5) * 50
        );
        
        Particle.DustOptions dust = new Particle.DustOptions(
            Color.fromRGB(
                (int)(Math.random() * 255),
                (int)(Math.random() * 255),
                (int)(Math.random() * 255)
            ),
            1.0f
        );
        
        //globalFlyLocation.getWorld().spawnParticle(
            //Particle.REDSTONE,
           // start,
           // 5,
           // 0.1, 0.1, 0.1,
           // dust
        //);
    }

    private void updateHologram(int percentage) {
        int cost = plugin.getConfig().getInt("Utils3.GlobalFlyCost", 100000);
        
        String info = ChatColor.translateAlternateColorCodes('&', 
            "&e{MONEY}$&3/&6{REQUIRED}$ &7({PERCENT}%%)");
        info = info.replace("{MONEY}", String.valueOf(globalFlyCash));
        info = info.replace("{REQUIRED}", String.valueOf(cost));
        info = info.replace("{PERCENT}", String.valueOf(percentage));
        
        // Update hologram
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dh line set ServerFly 1 8 " + info);
    }

    public void stopGlobalFly() {
        globalFlyStartTime = null;
        
        // Broadcast messages
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("");
            String title = plugin.getConfig().getString("Utils3.GlobalFlyTitle", "&6&lGLOBAL FLY");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', title));
            player.sendMessage("");
            
            String line1 = plugin.getConfig().getString("Utils3.GlobalFlyEnd.1", "&cGlobal fly ended!");
            String line2 = plugin.getConfig().getString("Utils3.GlobalFlyEnd.2", "&cFly disabled for all players");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line1));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line2));
            player.sendMessage("");
            
            player.playSound(player.getLocation(), Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 1.0f, 2.0f);
            
            // Disable fly for non-permission players
            if (!player.hasPermission("essentials.fly")) {
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        }
        
        // Update hologram
        String donateTitle = plugin.getConfig().getString("Utils3.DonateTitle", "&7Donate for Global Fly");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dh line set ServerFly 1 10 " + donateTitle);
        
        updateHologram(0);
    }

    private void startAutoChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (globalFlyStartTime != null) {
                    long diff = System.currentTimeMillis() - globalFlyStartTime;
                    if (diff >= 30 * 60 * 1000) { // 30 minut
                        stopGlobalFly();
                    }
                }
            }
        }.runTaskTimer(plugin, 200L, 200L); // Co 10 sekund
    }

    public boolean isGlobalFlyActive() {
        return globalFlyStartTime != null;
    }

    public int getGlobalFlyCash() {
        return globalFlyCash;
    }

    public void setGlobalFlyLocation(Location location) {
        this.globalFlyLocation = location;
        
        // Save to config
        plugin.getConfig().set("GlobalFly.Location.world", location.getWorld().getName());
        plugin.getConfig().set("GlobalFly.Location.x", location.getX());
        plugin.getConfig().set("GlobalFly.Location.y", location.getY());
        plugin.getConfig().set("GlobalFly.Location.z", location.getZ());
        plugin.saveConfig();
    }

    public void openGlobalFlyGUI(Player player) {
        if (isGlobalFlyActive()) {
            String message = plugin.getConfig().getString("Utils2.GlobalFlyEnabled", "&aGlobal fly is already active!");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.2f);
            return;
        }
        
        String title = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("GUIS.GlobalFly.Default", "&8Global Fly"));
        
        org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, 27, title);
        
        int[] slots = {11, 12, 13, 14, 15};
        int[] amounts = {1000, 5000, 10000, 25000, 100000};
        
        for (int i = 0; i < slots.length; i++) {
            String itemConfig = "GUIS.GlobalFly.Slot" + (i + 1);
            if (plugin.getConfig().contains(itemConfig)) {
                org.bukkit.inventory.ItemStack item = plugin.getConfig().getItemStack(itemConfig);
                if (item != null) {
                    inv.setItem(slots[i], item);
                }
            }
        }
        
        player.openInventory(inv);
    }
}
