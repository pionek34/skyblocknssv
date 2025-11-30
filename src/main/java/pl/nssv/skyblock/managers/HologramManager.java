package pl.nssv.skyblock.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import pl.nssv.skyblock.SkyblocknNSSV;

import java.util.*;

public class HologramManager {
    
    private final SkyblocknNSSV plugin;
    private ItemDisplay hologramEntity;
    private Location hologramLocation;
    private double hologramHeight;
    private BukkitRunnable animationTask;
    private final Map<UUID, Boolean> visibility = new HashMap<>();
    
    public HologramManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    public void loadHologram() {
        // Sprobuj załadowac lokalizacje z configu
        if (plugin.getConfig().contains("Hologram.Location")) {
            String world = plugin.getConfig().getString("Hologram.Location.world");
            double x = plugin.getConfig().getDouble("Hologram.Location.x");
            double y = plugin.getConfig().getDouble("Hologram.Location.y");
            double z = plugin.getConfig().getDouble("Hologram.Location.z");
            
            if (plugin.getServer().getWorld(world) != null) {
                hologramLocation = new Location(plugin.getServer().getWorld(world), x, y, z);
                createHologram(hologramLocation);
            }
        }
    }
    
    public void createHologram(Location location) {
        if (hologramEntity != null) {
            hologramEntity.remove();
        }
        
        hologramLocation = location.clone();
        hologramHeight = location.getY();
        
        // Zapisz lokalizacje do configu
        plugin.getConfig().set("Hologram.Location.world", location.getWorld().getName());
        plugin.getConfig().set("Hologram.Location.x", location.getX());
        plugin.getConfig().set("Hologram.Location.y", location.getY());
        plugin.getConfig().set("Hologram.Location.z", location.getZ());
        plugin.saveConfig();
        
        // Spawn item display
        hologramEntity = location.getWorld().spawn(location, ItemDisplay.class, entity -> {
            entity.setCustomName("Skyblock main Hologram");
            entity.setCustomNameVisible(false);
            
            // Ustaw item (gold nugget z custom model data)
            ItemStack item = new ItemStack(Material.GOLD_NUGGET);
            entity.setItemStack(item);
            
            // Ustaw transformacje
            entity.setInterpolationDuration(10);
            entity.setInterpolationDelay(0);
            entity.setTeleportDuration(2);
            
            Transformation transformation = entity.getTransformation();
            transformation.getScale().set(0, 0, 0);
            entity.setTransformation(transformation);
        });
        
        // Animacja pojawienia sie
        bounceIn();
        
        // Uruchom animacje
        startAnimation();
    }
    
    private void bounceIn() {
        double[] bounceValues = {0, 0.1, 0.25, 0.45, 0.7, 0.9, 1.02, 1.08, 1.1, 1.09, 1.07, 1.05, 1.03, 1.01, 1.0};
        
        new BukkitRunnable() {
            int tick = 0;
            
            @Override
            public void run() {
                if (tick >= bounceValues.length || hologramEntity == null || !hologramEntity.isValid()) {
                    cancel();
                    return;
                }
                
                double scale = bounceValues[tick] * 2;
                Transformation transformation = hologramEntity.getTransformation();
                transformation.getScale().set((float) scale, (float) scale, (float) scale);
                hologramEntity.setTransformation(transformation);
                
                hologramEntity.setInterpolationDuration(1);
                hologramEntity.setInterpolationDelay(0);
                
                tick++;
            }
        }.runTaskTimer(plugin, 2, 1);
    }
    
    private void startAnimation() {
        if (animationTask != null) {
            animationTask.cancel();
        }
        
        animationTask = new BukkitRunnable() {
            boolean up = true;
            
            @Override
            public void run() {
                if (hologramEntity == null || !hologramEntity.isValid()) {
                    if (hologramLocation != null) {
                        createHologram(hologramLocation);
                    }
                    return;
                }
                
                // Sprawdz wysokosc i napraw jesli potrzeba
                if (Math.abs(hologramEntity.getLocation().getY() - hologramHeight) > 0.1) {
                    fixHologram();
                }
                
                // Animacja gore/dol
                holoAnimation(up);
                up = !up;
            }
        };
        animationTask.runTaskTimer(plugin, 0, 68);
    }
    
    private void holoAnimation(boolean up) {
        double[] easeValues = {0.1, 0.22, 0.36, 0.52, 0.68, 0.82, 0.92, 0.98, 1, 0.98, 0.92, 0.82, 0.68, 0.52, 0.36, 0.22, 0.1};
        
        new BukkitRunnable() {
            int tick = 0;
            
            @Override
            public void run() {
                if (tick >= easeValues.length || hologramEntity == null || !hologramEntity.isValid()) {
                    cancel();
                    return;
                }
                
                Location loc = hologramEntity.getLocation();
                double value = easeValues[tick] / 14.0;
                
                if (up) {
                    loc.add(0, value, 0);
                } else {
                    loc.subtract(0, value, 0);
                }
                
                hologramEntity.teleport(loc);
                tick++;
            }
        }.runTaskTimer(plugin, up ? 0 : 34, 2);
    }
    
    private void fixHologram() {
        if (hologramEntity != null && hologramLocation != null) {
            Location fixed = hologramLocation.clone();
            fixed.setY(hologramHeight);
            hologramEntity.teleport(fixed);
        }
    }
    
    public void checkDistance(Player player) {
        if (hologramEntity == null || !hologramEntity.isValid()) return;
        
        double distance = player.getLocation().distance(hologramEntity.getLocation());
        
        if (distance >= 25) {
            hideHologram(player);
        } else {
            showHologram(player);
        }
    }
    
    public void showHologram(Player player) {
        if (!visibility.getOrDefault(player.getUniqueId(), false)) {
            visibility.put(player.getUniqueId(), true);
            if (hologramEntity != null) {
                player.showEntity(plugin, hologramEntity);
            }
        }
    }
    
    public void hideHologram(Player player) {
        if (visibility.getOrDefault(player.getUniqueId(), true)) {
            visibility.put(player.getUniqueId(), false);
            if (hologramEntity != null) {
                player.hideEntity(plugin, hologramEntity);
            }
        }
    }
    
    public void removeHologram() {
        if (hologramEntity != null) {
            hologramEntity.remove();
            hologramEntity = null;
        }
        
        if (animationTask != null) {
            animationTask.cancel();
            animationTask = null;
        }
        
        hologramLocation = null;
        visibility.clear();
        
        plugin.getConfig().set("Hologram.Location", null);
        plugin.saveConfig();
    }
    
    public void cleanup() {
        if (animationTask != null) {
            animationTask.cancel();
        }
        if (hologramEntity != null) {
            hologramEntity.remove();
        }
        visibility.clear();
    }
    
    public ItemDisplay getHologramEntity() {
        return hologramEntity;
    }
}
