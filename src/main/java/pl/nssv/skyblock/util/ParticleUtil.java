package pl.nssv.skyblock.util;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nssv.skyblock.SkyblocknNSSV;

public class ParticleUtil {
    
    public static void rankEffect(SkyblocknNSSV plugin, Location center) {
        new BukkitRunnable() {
            int count = 0;
            
            @Override
            public void run() {
                if (count >= 8) {
                    cancel();
                    return;
                }
                
                Location start = center.clone().add(
                    (Math.random() - 0.5) * 50,
                    (Math.random() - 0.5) * 50,
                    (Math.random() - 0.5) * 50
                );
                
                Color color = Color.fromRGB(
                    (int)(Math.random() * 255),
                    (int)(Math.random() * 255),
                    (int)(Math.random() * 255)
                );
                
                //center.getWorld().spawnParticle(
                        //
                    //Particle.REDSTONE,
                    //start,
                    //5,
                   // 0.1, 0.1, 0.1,
                   // new Particle.DustOptions(color, 1.0f)
               // );
                
                count++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
    
    public static void showCurve(SkyblocknNSSV plugin, Player player, Location start, Location end) {
        new BukkitRunnable() {
            double progress = 0;
            
            @Override
            public void run() {
                if (progress >= 1.0) {
                    cancel();
                    return;
                }
                
                double x = start.getX() + (end.getX() - start.getX()) * progress;
                double y = start.getY() + (end.getY() - start.getY()) * progress + Math.sin(progress * Math.PI) * 3;
                double z = start.getZ() + (end.getZ() - start.getZ()) * progress;
                
                Location particleLoc = new Location(start.getWorld(), x, y, z);
                start.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0);
                
                progress += 0.05;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    public static void spiralEffect(Location center, Particle particle, int duration) {
        new BukkitRunnable() {
            double angle = 0;
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= duration) {
                    cancel();
                    return;
                }
                
                double x = Math.cos(angle) * 1.5;
                double z = Math.sin(angle) * 1.5;
                double y = (ticks / (double) duration) * 2;
                
                Location particleLoc = center.clone().add(x, y, z);
                center.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
                
                angle += 0.3;
                ticks++;
            }
        }.runTaskTimer(SkyblocknNSSV.getInstance(), 0L, 1L);
    }
    
    public static void circleEffect(Location center, Particle particle, double radius, int points) {
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            
            Location particleLoc = center.clone().add(x, 0, z);
            center.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
        }
    }
}
