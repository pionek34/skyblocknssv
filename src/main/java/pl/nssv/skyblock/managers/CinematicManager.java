package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pl.nssv.skyblock.SkyblocknNSSV;

import java.util.*;

public class CinematicManager {
    private final SkyblocknNSSV plugin;
    
    // Cinematic data
    private final Set<String> cinematics = new HashSet<>();
    private final Map<String, List<Location>> cinematicPoints = new HashMap<>();
    private final Map<String, Map<Integer, Double>> cinematicTimes = new HashMap<>();
    private final Map<String, Map<Integer, Map<Integer, String>>> cinematicEffects = new HashMap<>();
    private final Map<String, Map<Integer, Map<Integer, String>>> cinematicSounds = new HashMap<>();
    
    // Player state
    private final Map<UUID, Integer> skipStage = new HashMap<>();
    private final Map<UUID, GameMode> previousGameMode = new HashMap<>();
    private final Map<UUID, Location> previousLocation = new HashMap<>();
    
    public CinematicManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
        
        // Create default "Welcome" cinematic
        createWelcomeCinematic();
    }
    
    private void createWelcomeCinematic() {
        cinematics.add("Welcome");
        
        // Default welcome points (example)
        List<Location> points = new ArrayList<>();
        World world = Bukkit.getWorld("world");
        if (world != null) {
            points.add(new Location(world, 0, 100, 0, 0, 0));
            points.add(new Location(world, 10, 100, 10, 45, 0));
            points.add(new Location(world, 20, 105, 20, 90, -10));
        }
        cinematicPoints.put("Welcome", points);
    }
    
    public boolean createCinematic(String name) {
        if (cinematics.contains(name)) {
            return false;
        }
        cinematics.add(name);
        cinematicPoints.put(name, new ArrayList<>());
        cinematicTimes.put(name, new HashMap<>());
        return true;
    }
    
    public boolean deleteCinematic(String name) {
        if (!cinematics.contains(name) || name.equals("Welcome")) {
            return false;
        }
        cinematics.remove(name);
        cinematicPoints.remove(name);
        cinematicTimes.remove(name);
        cinematicEffects.remove(name);
        cinematicSounds.remove(name);
        return true;
    }
    
    public void addPoint(String cinematic, Location location, double time) {
        List<Location> points = cinematicPoints.computeIfAbsent(cinematic, k -> new ArrayList<>());
        points.add(location);
        
        Map<Integer, Double> times = cinematicTimes.computeIfAbsent(cinematic, k -> new HashMap<>());
        times.put(points.size() - 1, time);
    }
    
    public void startCinematic(Player player, String cinematicName, boolean particleMode) {
        if (!cinematics.contains(cinematicName)) {
            player.sendMessage(ChatColor.RED + "Cinematic doesn't exist!");
            return;
        }
        
        skipStage.put(player.getUniqueId(), 6);
        previousGameMode.put(player.getUniqueId(), player.getGameMode());
        previousLocation.put(player.getUniqueId(), player.getLocation());
        
        List<Location> points = cinematicPoints.get(cinematicName);
        if (points == null || points.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Cinematic has no points!");
            endCinematic(player);
            return;
        }
        
        // Special welcome message
        if (cinematicName.equals("Welcome")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    //player.sendActionBar(ChatColor.YELLOW + "Press SHIFT 6 times to skip");
                }
            }.runTaskLater(plugin, 60L);
        }
        
        runCinematicSequence(player, cinematicName, points, particleMode);
    }
    
    private void runCinematicSequence(Player player, String cinematicName, List<Location> points, boolean particleMode) {
        new BukkitRunnable() {
            int currentPoint = 0;
            Display cameraEntity = null;
            
            @Override
            public void run() {
                // Check if player skipped
                if (!skipStage.containsKey(player.getUniqueId()) || skipStage.get(player.getUniqueId()) < 1) {
                    cleanup();
                    cancel();
                    return;
                }
                
                if (currentPoint >= points.size() - 1) {
                    cleanup();
                    cancel();
                    endCinematic(player);
                    return;
                }
                
                Location start = points.get(currentPoint);
                Location end = points.get(currentPoint + 1);
                
                // Create camera entity if needed
                if (cameraEntity == null && !particleMode) {
                    cameraEntity = (Display) start.getWorld().spawnEntity(start, EntityType.BLOCK_DISPLAY);
                    cameraEntity.setCustomName("Cinematic-" + player.getName());
                    cameraEntity.setTeleportDuration(15);
                    
                    player.setGameMode(GameMode.SPECTATOR);
                    player.setSpectatorTarget(cameraEntity);
                }
                
                // Interpolate between points
                Map<Integer, Double> times = cinematicTimes.get(cinematicName);
                double duration = times != null ? times.getOrDefault(currentPoint, 5.0) : 5.0;
                
                interpolateMovement(player, cameraEntity, start, end, duration, particleMode, currentPoint);
                
                currentPoint++;
            }
            
            private void cleanup() {
                if (cameraEntity != null) {
                    cameraEntity.remove();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private void interpolateMovement(Player player, Display camera, Location start, Location end, 
                                    double duration, boolean particleMode, int pointIndex) {
        int steps = (int) (duration * 20); // Convert to ticks
        
        new BukkitRunnable() {
            int step = 0;
            
            @Override
            public void run() {
                if (!skipStage.containsKey(player.getUniqueId()) || skipStage.get(player.getUniqueId()) < 1) {
                    cancel();
                    return;
                }
                
                if (step >= steps) {
                    cancel();
                    return;
                }
                
                double mu = step / (double) steps;
                Location interpolated = interpolate(start, end, mu);
                
                if (particleMode) {
                    // Show particles for editing mode
                    start.getWorld().spawnParticle(Particle.END_ROD, interpolated, 1, 0, 0, 0, 0);
                } else if (camera != null) {
                    camera.teleport(interpolated.clone().add(0, 1.5, 0));
                }
                
                step++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private Location interpolate(Location start, Location end, double mu) {
        // Linear interpolation for simplicity
        double x = start.getX() + (end.getX() - start.getX()) * mu;
        double y = start.getY() + (end.getY() - start.getY()) * mu;
        double z = start.getZ() + (end.getZ() - start.getZ()) * mu;
        
        float yaw = (float) (start.getYaw() + (end.getYaw() - start.getYaw()) * mu);
        float pitch = (float) (start.getPitch() + (end.getPitch() - start.getPitch()) * mu);
        
        return new Location(start.getWorld(), x, y, z, yaw, pitch);
    }
    
    /**
     * Catmull-Rom spline interpolation for smooth camera movement
     */
    private double catmullRom(double y0, double y1, double y2, double y3, double mu) {
        double a0 = -0.5*y0 + 1.5*y1 - 1.5*y2 + 0.5*y3;
        double a1 = y0 - 2.5*y1 + 2*y2 - 0.5*y3;
        double a2 = -0.5*y0 + 0.5*y2;
        double a3 = y1;
        
        return a0 * mu * mu * mu + a1 * mu * mu + a2 * mu + a3;
    }
    
    /**
     * Hermite spline interpolation
     */
    private double hermite(double y0, double y1, double y2, double y3, double mu, double tension, double bias) {
        double m0 = (y1 - y0) * (1 + bias) * (1 - tension) / 2;
        m0 += (y2 - y1) * (1 - bias) * (1 - tension) / 2;
        
        double m1 = (y2 - y1) * (1 + bias) * (1 - tension) / 2;
        m1 += (y3 - y2) * (1 - bias) * (1 - tension) / 2;
        
        double mu2 = mu * mu;
        double mu3 = mu2 * mu;
        
        double a0 = 2 * mu3 - 3 * mu2 + 1;
        double a1 = mu3 - 2 * mu2 + mu;
        double a2 = mu3 - mu2;
        double a3 = -2 * mu3 + 3 * mu2;
        
        return a0 * y1 + a1 * m0 + a2 * m1 + a3 * y2;
    }
    
    public void handleSkip(Player player) {
        UUID uuid = player.getUniqueId();
        if (!skipStage.containsKey(uuid)) {
            return;
        }
        
        int stage = skipStage.get(uuid);
        if (stage > 0) {
            skipStage.put(uuid, stage - 1);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 
                           (float) (1.3 + (6 - stage) / 8.0));
            
            if (stage <= 1) {
                endCinematic(player);
            }
        }
    }
    
    private void endCinematic(Player player) {
        UUID uuid = player.getUniqueId();
        
        skipStage.remove(uuid);
        
        GameMode previousMode = previousGameMode.remove(uuid);
        if (previousMode != null) {
            player.setGameMode(previousMode);
        }
        
        Location previousLoc = previousLocation.remove(uuid);
        if (previousLoc != null) {
            player.teleport(previousLoc);
        }
        
        player.setSpectatorTarget(null);
        
        // Remove any camera entities
        for (Entity entity : player.getWorld().getEntities()) {
            if (entity.getCustomName() != null && 
                entity.getCustomName().equals("Cinematic-" + player.getName())) {
                entity.remove();
            }
        }
    }
    
    // Getters
    public Set<String> getCinematics() {
        return cinematics;
    }
    
    public boolean isInCinematic(Player player) {
        return skipStage.containsKey(player.getUniqueId());
    }
    
    public int getSkipStage(Player player) {
        return skipStage.getOrDefault(player.getUniqueId(), 0);
    }
}
