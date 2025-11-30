package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.data.PlayerData;

import java.util.*;

public class BossbarStatsManager {
    private final SkyblocknNSSV plugin;
    
    // Bossbar tracking
    private final Map<UUID, BossBar> stats1Bars = new HashMap<>(); // Main stats
    private final Map<UUID, BossBar> stats2Bars = new HashMap<>(); // Event title
    private final Map<UUID, BossBar> stats3Bars = new HashMap<>(); // Objectives
    
    // Text background cache
    private final Map<String, String> backgroundCache = new HashMap<>();
    
    // Unicode characters for text backgrounds
    private final List<Integer> availableSizes = Arrays.asList(128, 64, 32, 16, 12, 6, 5, 4, 3, 2, 1);
    private final Map<Integer, String> unicodeChars = new HashMap<>();
    
    // Update tracking
    private final Map<UUID, Double> lastDataHash = new HashMap<>();
    private int updateCounter = 0;
    
    public BossbarStatsManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
        
        // Initialize unicode characters (example - would need actual unicode from resource pack)
        initializeUnicodeChars();
        
        // Start update task
        startUpdateTask();
    }
    
    private void initializeUnicodeChars() {
        // For now using placeholders
        unicodeChars.put(128, "\uE000");
        unicodeChars.put(64, "\uE001");
        unicodeChars.put(32, "\uE002");
        unicodeChars.put(16, "\uE003");
        unicodeChars.put(12, "\uE004");
        unicodeChars.put(6, "\uE005");
        unicodeChars.put(5, "\uE006");
        unicodeChars.put(4, "\uE007");
        unicodeChars.put(3, "\uE008");
        unicodeChars.put(2, "\uE009");
        unicodeChars.put(1, "\uE010");
    }
    
    private void startUpdateTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateCounter++;
                
                // Update every 3 seconds normally, faster during events
                if (updateCounter >= 3) {
                    updateCounter = 0;
                    
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        updateIfNeeded(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Every second
    }
    
    private void updateIfNeeded(Player player) {
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        
        // Calculate hash to check if update needed
        //double hash = data.getMoney() + data.getGems() + data.getLevel();
        
        //Double lastHash = lastDataHash.get(player.getUniqueId());
        //if (lastHash == null || lastHash != hash) {
            //updateBossbar(player);
            //updateObjective(player);
            //lastDataHash.put(player.getUniqueId(), hash);
        //}
    }
    
    public void createBossbars(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                removeBossbars(player);
                
                // Stats1 - Main stats (money, level, gems)
                BossBar stats1 = Bukkit.createBossBar(
                    ChatColor.GRAY + "",
                    BarColor.WHITE,
                    BarStyle.SOLID
                );
                stats1.addPlayer(player);
                stats1.setProgress(1.0);
                stats1Bars.put(player.getUniqueId(), stats1);
                
                // Stats2 - Event title
                BossBar stats2 = Bukkit.createBossBar(
                    ChatColor.GRAY + "",
                    BarColor.WHITE,
                    BarStyle.SOLID
                );
                stats2.addPlayer(player);
                stats2.setProgress(1.0);
                stats2Bars.put(player.getUniqueId(), stats2);
                
                // Stats3 - Objectives
                String objective = plugin.getObjectiveManager().getObjective(player);
                if (objective == null || !objective.equals("End")) {
                    BossBar stats3 = Bukkit.createBossBar(
                        ChatColor.GRAY + "",
                        BarColor.WHITE,
                        BarStyle.SOLID
                    );
                    stats3.addPlayer(player);
                    stats3.setProgress(1.0);
                    stats3Bars.put(player.getUniqueId(), stats3);
                }
                
                updateBossbar(player);
                updateObjective(player);
            }
        }.runTaskLater(plugin, 20L);
    }
    
    public void removeBossbars(Player player) {
        UUID uuid = player.getUniqueId();
        
        BossBar bar1 = stats1Bars.remove(uuid);
        if (bar1 != null) {
            bar1.removeAll();
        }
        
        BossBar bar2 = stats2Bars.remove(uuid);
        if (bar2 != null) {
            bar2.removeAll();
        }
        
        BossBar bar3 = stats3Bars.remove(uuid);
        if (bar3 != null) {
            bar3.removeAll();
        }
        
        lastDataHash.remove(uuid);
    }
    
    public void updateBossbar(Player player) {
        BossBar bar = stats1Bars.get(player.getUniqueId());
        if (bar == null) return;
        
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        
        // Format stats with text backgrounds
        //String moneyText = textBackground(
            //String.format("<##FFFEFD> <##f9e300>$%.2f ", data.getMoney()),
            //player
        //);
        
        String levelText = textBackground(
            String.format("<##FFFEFD> <##7dfc32>%d ", data.getLevel()),
            player
        );
        
        String gemsText = textBackground(
            String.format("<##FFFEFD> <##a9e8f7>%d ", data.getGems()),
            player
        );
        
        String eventTimer = "";
        // Event timer would go here if global event is active
        
        //String title = eventTimer + "  " + gemsText + "  " + levelText + "  " + moneyText;
        //bar.setTitle(title);
    }
    
    public void updateObjective(Player player) {
        BossBar bar = stats3Bars.get(player.getUniqueId());
        if (bar == null) return;
        
        String objective = plugin.getObjectiveManager().getObjective(player);
        
        if (objective == null || objective.contains("Wait")) {
            bar.setTitle("  " + ChatColor.GOLD + "°");
            return;
        }
        
        if (objective.equals("End")) {
            bar.setTitle("");
            return;
        }
        
        // Get objective text from config
        String objectiveText = "Complete objective"; // Would get from config
        
        // Add progress if it's a quest objective
        List<String> questObjectives = Arrays.asList(
            "Collect4Logs", "Mine16Iron", "Mine8Gold", "Mine64Cobble",
            "ExpandIsland1", "ExpandIsland2", "Mine128Cobble", "Mine16Diamond", "Mine64Ores"
        );
        
        if (questObjectives.contains(objective)) {
            //int progress = plugin.getQuestManager().getQuestProgress(player, objective);
            //objectiveText = objectiveText.replace("{AMOUNT}", String.valueOf(progress));
        }
        
        String formatted = textBackground(ChatColor.GOLD + objectiveText, player);
        bar.setTitle("  " + formatted);
    }
    
    public void showEventTitle(Player player, String text) {
        BossBar bar = stats2Bars.get(player.getUniqueId());
        if (bar == null) return;
        
        // Remove and recreate stats3 bar
        BossBar stats3 = stats3Bars.remove(player.getUniqueId());
        if (stats3 != null) {
            stats3.removeAll();
        }
        
        String objective = plugin.getObjectiveManager().getObjective(player);
        if (objective == null || !objective.equals("End")) {
            BossBar newStats3 = Bukkit.createBossBar(
                ChatColor.GRAY + "",
                BarColor.WHITE,
                BarStyle.SOLID
            );
            newStats3.addPlayer(player);
            newStats3.setProgress(1.0);
            stats3Bars.put(player.getUniqueId(), newStats3);
        }
        
        // Animate event title
        new BukkitRunnable() {
            int step = 0;
            int maxSteps = (int) (text.length() / 1.285);
            
            @Override
            public void run() {
                if (step >= maxSteps) {
                    // Flash title
                    flashEventTitle(player, text, bar);
                    cancel();
                    return;
                }
                
                StringBuilder spaces = new StringBuilder();
                for (int i = 0; i < step; i++) {
                    if (i % 10 != 0) {
                        spaces.append("  ");
                    }
                }
                
                bar.setTitle(textBackground(spaces.toString(), player));
                step++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
    
    private void flashEventTitle(Player player, String text, BossBar bar) {
        new BukkitRunnable() {
            int flashes = 0;
            
            @Override
            public void run() {
                if (flashes >= 7) {
                    cancel();
                    return;
                }
                
                String color = (flashes % 2 == 0) ? "<##FFFFFD> &e" : "<##FFFFFD> &f";
                String title = textBackground(color + text + " ", player);
                bar.setTitle(title);
                
                flashes++;
            }
        }.runTaskTimer(plugin, 0L, 6L);
    }
    
    /**
     * Create text background using custom unicode characters
     * This adds a visual background behind text in the bossbar
     */
    private String textBackground(String text, Player player) {
        // Check if player is Bedrock (no unicode support)
        if (player.getName().startsWith(".")) {
            return text.replace("@", "");
        }
        
        String uncolored = ChatColor.stripColor(text);
        
        // Check cache
        if (backgroundCache.containsKey(uncolored)) {
            String cached = backgroundCache.get(uncolored);
            return ChatColor.translateAlternateColorCodes('&', 
                "<##FFFEFD>" + cached + "&r" + text);
        }
        
        // Calculate background width
        int width = calculateTextWidth(uncolored);
        String background = createBackground(width);
        
        // Cache if reasonable size
        if (background.length() < 30) {
            backgroundCache.put(uncolored, background);
        }
        
        return ChatColor.translateAlternateColorCodes('&',
            "<##FFFEFD>" + background + "&r" + text);
    }
    
    private int calculateTextWidth(String text) {
        int width = 0;
        
        for (char c : text.toCharArray()) {
            if (c == 'i' || c == '!' || c == ';' || c == ':' || c == '.' || c == '|' || c == ',' || c == '@') {
                width += Character.isUpperCase(c) ? 4 : 2;
            } else if (c == 'l') {
                width += 3;
            } else if (c == 't' || c == ' ' || c == '(' || c == ')' || c == '*' || c == '[' || c == ']') {
                width += 4;
            } else if (c == 'f' || c == 'k') {
                width += 5;
            } else {
                width += 6; // Default
            }
        }
        
        return width;
    }
    
    private String createBackground(int targetWidth) {
        StringBuilder result = new StringBuilder();
        int remaining = targetWidth;
        
        for (int size : availableSizes) {
            while (remaining >= size) {
                result.append(unicodeChars.getOrDefault(size, ""));
                remaining -= size;
            }
            if (remaining <= 0) break;
        }
        
        return result.toString();
    }
    
    public void playSound(Player player, String soundType) {
        if (soundType.equals("EventStart")) {
            for (int i = 0; i < 2; i++) {
                player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 0.4f, 1.0f);
                for (int j = 0; j < 3; j++) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 0.4f, 2.0f);
                }
            }
            
            new BukkitRunnable() {
                int tick = 0;
                
                @Override
                public void run() {
                    if (tick >= 5) {
                        cancel();
                        return;
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.8f, 
                                   (float) (0.9 + tick / 10.0));
                    tick++;
                }
            }.runTaskTimer(plugin, 3L, 2L);
        }
        
        if (soundType.equals("EventEnd")) {
            for (int i = 0; i < 2; i++) {
                player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 0.4f, 1.0f);
                for (int j = 0; j < 3; j++) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.4f, 2.0f);
                }
            }
            
            new BukkitRunnable() {
                int tick = 0;
                
                @Override
                public void run() {
                    if (tick >= 5) {
                        cancel();
                        return;
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.8f,
                                   (float) (1.5 - tick / 10.0));
                    tick++;
                }
            }.runTaskTimer(plugin, 3L, 2L);
        }
    }
}
