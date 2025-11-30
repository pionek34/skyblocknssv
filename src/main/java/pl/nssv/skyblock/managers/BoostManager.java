package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nssv.skyblock.SkyblocknNSSV;
import java.util.*;

public class BoostManager {
    private final SkyblocknNSSV plugin;
    private final List<Location> boostLocations = new ArrayList<>();
    private final Map<Location, ArmorStand> activeBoosts = new HashMap<>();
    private final Map<UUID, Long> boostCooldowns = new HashMap<>();

    public BoostManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
        loadBoostLocations();
        startAutoSpawn();
    }

    private void loadBoostLocations() {
        if (plugin.getConfig().contains("Boosts.Locations")) {
            List<Map<?, ?>> locs = plugin.getConfig().getMapList("Boosts.Locations");
            for (Map<?, ?> locMap : locs) {
                try {
                    World world = Bukkit.getWorld((String) locMap.get("world"));
                    double x = ((Number) locMap.get("x")).doubleValue();
                    double y = ((Number) locMap.get("y")).doubleValue();
                    double z = ((Number) locMap.get("z")).doubleValue();
                    boostLocations.add(new Location(world, x, y, z));
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load boost location!");
                }
            }
        }
    }

    private void startAutoSpawn() {
        new BukkitRunnable() {
            @Override
            public void run() {
                spawnRandomBoost();
            }
        }.runTaskTimer(plugin, 3600L, 3600L);
    }

    private void spawnRandomBoost() {
        if (boostLocations.isEmpty()) return;
        
        Location loc = boostLocations.get(new Random().nextInt(boostLocations.size()));
        
        ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCustomName(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("Boosts.Name", "&6&lBOOST PAD")));
        stand.setCustomNameVisible(true);
        
        activeBoosts.put(loc, stand);
        
        spawnBoostAnimation(loc);
        
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("Boosts.Spawned", "&eBoost pad spawned!")));
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.5f);
        }
        
        new BukkitRunnable() {
            @Override
            public void run() {
                removeBoost(loc);
            }
        }.runTaskLater(plugin, 1200L);
    }

    private void spawnBoostAnimation(Location loc) {
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 60 || !activeBoosts.containsKey(loc)) {
                    cancel();
                    return;
                }
                
                double angle = ticks * 0.3;
                for (int i = 0; i < 3; i++) {
                    double offsetAngle = angle + (i * 120);
                    double x = Math.cos(offsetAngle) * 1.5;
                    double z = Math.sin(offsetAngle) * 1.5;
                    double y = Math.sin(ticks * 0.2) * 0.5;
                    
                    Location particleLoc = loc.clone().add(x, y + 1, z);
                    loc.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void removeBoost(Location loc) {
        ArmorStand stand = activeBoosts.remove(loc);
        if (stand != null && !stand.isDead()) {
            stand.remove();
        }
    }

    public void checkBoostCollection(Player player, Location location) {
        for (Map.Entry<Location, ArmorStand> entry : activeBoosts.entrySet()) {
            if (entry.getKey().distance(location) <= 2.0) {
                collectBoost(player, entry.getKey());
                return;
            }
        }
    }

    private void collectBoost(Player player, Location loc) {
        UUID uuid = player.getUniqueId();
        
        Long lastCollect = boostCooldowns.get(uuid);
        if (lastCollect != null && System.currentTimeMillis() - lastCollect < 5000) {
            return;
        }
        
        boostCooldowns.put(uuid, System.currentTimeMillis());
        removeBoost(loc);
        
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("Boosts.Collected", "&aCollected boost!")));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
        
        Random random = new Random();
        int type = random.nextInt(3);
        
        switch (type) {
            case 0:
                int money = random.nextInt(201) + 100;
                plugin.getEconomy().depositPlayer(player, money);
                player.sendMessage(ChatColor.GOLD + "+" + money + "$");
                break;
            case 1:
                int gems = random.nextInt(11) + 5;
                plugin.getDataManager().getPlayerData(player).setGems(
                    plugin.getDataManager().getPlayerData(player).getGems() + gems);
                player.sendMessage(ChatColor.AQUA + "+" + gems + " gems");
                break;
            case 2:
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
                    "crates key give " + player.getName() + " common 1");
                player.sendMessage(ChatColor.YELLOW + "+1 key");
                break;
        }
        
        boostCollectedEffect(player.getLocation());
    }

    private void boostCollectedEffect(Location loc) {
        loc.getWorld().spawnParticle(Particle.FIREWORK, loc, 50, 0.5, 0.5, 0.5, 0.1);
        loc.getWorld().playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 2.0f);
    }

    public void addBoostLocation(Location location) {
        boostLocations.add(location);
        saveBoostLocations();
    }

    private void saveBoostLocations() {
        List<Map<String, Object>> locs = new ArrayList<>();
        for (Location loc : boostLocations) {
            Map<String, Object> locMap = new HashMap<>();
            locMap.put("world", loc.getWorld().getName());
            locMap.put("x", loc.getX());
            locMap.put("y", loc.getY());
            locMap.put("z", loc.getZ());
            locs.add(locMap);
        }
        plugin.getConfig().set("Boosts.Locations", locs);
        plugin.saveConfig();
    }
}
