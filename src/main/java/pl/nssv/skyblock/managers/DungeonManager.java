package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nssv.skyblock.SkyblocknNSSV;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DungeonManager {
    private final SkyblocknNSSV plugin;
    
    // Dungeon state
    private boolean dungeonsOpen = false;
    private boolean dungeonsStarted = false;
    private boolean dungeonEnd = false;
    private boolean bossFight = false;
    
    // Players
    private final Set<UUID> dungeonPlayers = new HashSet<>();
    private final Map<UUID, BossBar> playerBossBars = new HashMap<>();
    
    // Dungeon progress
    private int keysCollected = 0;
    private int roomsCleared = 0;
    private int currentRoom = 0;
    private final Map<Integer, Integer> waveProgress = new HashMap<>();
    private int requiredKills = 0;
    private int currentKills = 0;
    
    // Boss
    private int bossHealth = 0;
    private int maxBossHealth = 1000;
    private boolean airAttackNext = true;
    
    // Locations
    private Location spawn;
    private final Map<Integer, Location> dungeonSpawns = new HashMap<>();
    private final Map<Integer, Location> doorLocations = new HashMap<>();
    private final Map<Integer, Location> chestLocations = new HashMap<>();
    
    // Configuration
    private int requiredPlayers = 3;
    private int counterSeconds = 10;
    private double globalDifficulty = 1.0;
    
    public DungeonManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
        startTimeChecker();
    }
    
    private void startTimeChecker() {
        // Check every 12 seconds for auto open/close
        new BukkitRunnable() {
            @Override
            public void run() {
                checkOpenTime();
                
                // Boss fight logic
                if (bossFight) {
                    handleBossFightCycle();
                }
            }
        }.runTaskTimer(plugin, 240L, 240L); // 12 seconds
    }
    
    private void checkOpenTime() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeString = now.format(formatter);
        
        // Check if time to open (example: 18:00, 20:00)
        List<String> openTimes = Arrays.asList("18:00", "20:00", "22:00");
        List<String> closeTimes = Arrays.asList("19:00", "21:00", "23:00");
        
        for (String openTime : openTimes) {
            if (timeString.equals(openTime) && !dungeonsOpen) {
                startDungeon();
                return;
            }
        }
        
        for (String closeTime : closeTimes) {
            if (timeString.equals(closeTime) && dungeonsOpen) {
                closeDungeons();
                return;
            }
        }
    }
    
    public void startDungeon() {
        dungeonsOpen = true;
        
        Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "DUNGEONS OPENED!");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Type /dungeon to join!");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        }
        
        // Create dungeon doors
        createDungeonDoors();
    }
    
    public void closeDungeons() {
        dungeonsOpen = false;
        dungeonsStarted = false;
        bossFight = false;
        
        Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "DUNGEONS CLOSED!");
        
        // Teleport all players out
        for (UUID uuid : new HashSet<>(dungeonPlayers)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && spawn != null) {
                player.teleport(spawn);
            }
        }
        
        dungeonPlayers.clear();
        resetDungeon();
    }
    
    public boolean joinDungeon(Player player) {
        if (!dungeonsOpen) {
            player.sendMessage(ChatColor.RED + "Dungeons are currently closed!");
            return false;
        }
        
        if (dungeonPlayers.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in the dungeon!");
            return false;
        }
        
        dungeonPlayers.add(player.getUniqueId());
        
        // Teleport to dungeon
        if (dungeonSpawns.containsKey(0)) {
            player.teleport(dungeonSpawns.get(0));
        }
        
        // Broadcast join
        String joinMessage = ChatColor.GREEN + player.getName() + " joined the dungeon! " +
                           ChatColor.GRAY + "(" + dungeonPlayers.size() + "/" + requiredPlayers + ")";
        broadcastToDungeon(joinMessage);
        
        // Check if ready to start
        if (!dungeonsStarted && dungeonPlayers.size() >= requiredPlayers) {
            startDungeonCountdown();
        } else if (!dungeonsStarted) {
            updateWaitingBossBar();
        }
        
        return true;
    }
    
    public void leaveDungeon(Player player) {
        if (!dungeonPlayers.remove(player.getUniqueId())) {
            return;
        }
        
        // Remove boss bars
        BossBar bar = playerBossBars.remove(player.getUniqueId());
        if (bar != null) {
            bar.removePlayer(player);
        }
        
        // Broadcast leave
        String leaveMessage = ChatColor.RED + player.getName() + " left the dungeon! " +
                            ChatColor.GRAY + "(" + dungeonPlayers.size() + " remaining)";
        broadcastToDungeon(leaveMessage);
        
        // Teleport to spawn
        if (spawn != null) {
            player.teleport(spawn);
        }
        
        // Check if too few players
        if (!dungeonsStarted && dungeonPlayers.size() < requiredPlayers) {
            cancelDungeonStart();
        }
    }
    
    private void startDungeonCountdown() {
        new BukkitRunnable() {
            int countdown = counterSeconds;
            
            @Override
            public void run() {
                if (dungeonPlayers.size() < requiredPlayers) {
                    cancel();
                    return;
                }
                
                if (countdown <= 0) {
                    actuallyStartDungeon();
                    cancel();
                    return;
                }
                
                for (UUID uuid : dungeonPlayers) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.sendTitle(ChatColor.GREEN + "Starting in " + countdown,
                                       ChatColor.YELLOW + "Get ready!", 0, 30, 10);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.5f);
                    }
                }
                
                countdown--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    
    private void actuallyStartDungeon() {
        dungeonsStarted = true;
        keysCollected = 0;
        roomsCleared = 0;
        currentRoom = 1;
        waveProgress.clear();
        
        broadcastToDungeon(ChatColor.GREEN + "" + ChatColor.BOLD + "DUNGEON STARTED!");
        
        for (UUID uuid : dungeonPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.9f);
            }
        }
        
        // Open first door after delay
        new BukkitRunnable() {
            @Override
            public void run() {
                openDoor(1);
            }
        }.runTaskLater(plugin, 60L);
    }
    
    private void cancelDungeonStart() {
        updateWaitingBossBar();
    }
    
    private void updateWaitingBossBar() {
        String title = ChatColor.YELLOW + "Waiting for players... " +
                      ChatColor.WHITE + dungeonPlayers.size() + "/" + requiredPlayers;
        double progress = dungeonPlayers.size() / (double) requiredPlayers;
        
        for (UUID uuid : dungeonPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                BossBar bar = playerBossBars.computeIfAbsent(uuid, 
                    k -> Bukkit.createBossBar(title, BarColor.RED, BarStyle.SOLID));
                bar.setTitle(title);
                bar.setProgress(Math.min(1.0, progress));
                bar.addPlayer(player);
            }
        }
    }
    
    private void broadcastToDungeon(String message) {
        for (UUID uuid : dungeonPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }
    
    private void createDungeonDoors() {
        // Create block display doors with animations
        // This would spawn the door entities
    }
    
    private void openDoor(int doorNumber) {
        broadcastToDungeon(ChatColor.GREEN + "Door " + doorNumber + " is opening!");
        
        for (UUID uuid : dungeonPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
            }
        }
        
        // Animate door opening
        // Remove door display entities
        
        // Start wave for this room
        if (doorNumber >= 2 && doorNumber <= 4) {
            startWave(doorNumber);
        }
    }
    
    private void startWave(int room) {
        int wave = waveProgress.getOrDefault(room, 0) + 1;
        waveProgress.put(room, wave);
        
        requiredKills = 20 + (wave * 5);
        currentKills = 0;
        
        broadcastToDungeon(ChatColor.YELLOW + "Wave " + wave + " starting in 5 seconds!");
        
        new BukkitRunnable() {
            @Override
            public void run() {
                spawnWaveMobs(room, wave);
            }
        }.runTaskLater(plugin, 100L);
    }
    
    private void spawnWaveMobs(int room, int wave) {
        Location spawnLoc = dungeonSpawns.get(room);
        if (spawnLoc == null) return;
        
        for (int i = 0; i < requiredKills + 3; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    EntityType type = Math.random() < 0.5 ? EntityType.ZOMBIE : EntityType.SKELETON;
                    LivingEntity mob = (LivingEntity) spawnLoc.getWorld().spawnEntity(spawnLoc, type);
                    
                    double health = 10 + (roomsCleared * 3);
                    mob.setMaxHealth(health);
                    mob.setHealth(health);
                    mob.setCustomName(ChatColor.RED + type.name());
                    
                    mob.getWorld().playSound(mob.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
                    mob.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION, 
                        mob.getLocation(), 7, 0.3, 0.3, 0.3, 0.1);
                }
            }.runTaskLater(plugin, i * 1L);
        }
    }
    
    public void handleMobKill() {
        currentKills++;
        
        if (currentKills >= requiredKills) {
            completeWave();
        }
        
        updateWaveBossBar();
    }
    
    private void completeWave() {
        int wave = waveProgress.get(currentRoom);
        
        if (wave >= 3) {
            roomsCleared++;
            keysCollected++;
            
            broadcastToDungeon(ChatColor.GREEN + "Room " + roomsCleared + " cleared!");
            broadcastToDungeon(ChatColor.YELLOW + "You received a key!");
            
            for (UUID uuid : dungeonPlayers) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
                }
            }
        }
    }
    
    private void updateWaveBossBar() {
        String title = ChatColor.YELLOW + "Next Wave: " + currentKills + "/" + requiredKills;
        double progress = currentKills / (double) requiredKills;
        
        for (UUID uuid : dungeonPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                BossBar bar = playerBossBars.get(uuid);
                if (bar != null) {
                    bar.setTitle(title);
                    bar.setProgress(Math.min(1.0, progress));
                }
            }
        }
    }
    
    private void handleBossFightCycle() {
        // Boss attack patterns every 12 seconds
        if (airAttackNext) {
            airAttack();
        } else {
            int action = (int) (Math.random() * 5) + 1;
            switch (action) {
                case 1: groundAttack(); break;
                case 2: spawnBossMobs(); break;
                case 4: spawnMiniPhantoms(); break;
                case 5: spawnLavaAttack(); break;
            }
        }
        
        airAttackNext = !airAttackNext;
    }
    
    private void airAttack() {
        // Air attack implementation
    }
    
    private void groundAttack() {
        // Ground attack with evoker fangs
    }
    
    private void spawnBossMobs() {
        // Spawn zombies/skeletons around boss
    }
    
    private void spawnMiniPhantoms() {
        // Spawn mini phantoms
    }
    
    private void spawnLavaAttack() {
        // Spawn lava display attack
    }
    
    private void resetDungeon() {
        dungeonPlayers.clear();
        playerBossBars.values().forEach(bar -> {
            bar.removeAll();
        });
        playerBossBars.clear();
        
        keysCollected = 0;
        roomsCleared = 0;
        currentRoom = 0;
        waveProgress.clear();
        requiredKills = 0;
        currentKills = 0;
        bossHealth = 0;
    }
    
    // Getters
    public boolean isDungeonsOpen() {
        return dungeonsOpen;
    }
    
    public Set<UUID> getDungeonPlayers() {
        return dungeonPlayers;
    }
    
    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }
    
    public void setDungeonSpawn(int room, Location location) {
        dungeonSpawns.put(room, location);
    }
}
