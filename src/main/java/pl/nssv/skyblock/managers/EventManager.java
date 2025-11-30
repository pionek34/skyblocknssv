package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nssv.skyblock.SkyblocknNSSV;

import java.util.*;

public class EventManager {
    private final SkyblocknNSSV plugin;
    private final Set<String> activeEvents = new HashSet<>();
    private String currentGlobalEvent = null;
    private int eventTimeLeft = 0;
    private final Map<UUID, BossBar> playerBossBars = new HashMap<>();

    public EventManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
        
        // Auto-start events co 30 minut
        startAutoEvents();
    }

    private void startAutoEvents() {
        new BukkitRunnable() {
            @Override
            public void run() {
                startEvent(null);
            }
        }.runTaskTimer(plugin, 36000L, 36000L); // 30 minut = 36000 ticków
    }

    public void startEvent(String selectedEvent) {
        // Losuj event jeśli nie podano
        String eventData;
        if (selectedEvent == null) {
            String[] events = {"DoubleXP", "DoubleCaveDrop", "MinionBoost", "JobBoost"};
            eventData = events[new Random().nextInt(events.length)];
        } else {
            eventData = selectedEvent;
        }
        
        String eventName = plugin.getConfig().getString("Utils3.EventName." + eventData, eventData);
        currentGlobalEvent = eventName;
        
        // Event message
        eventMessage(eventData);
        
        // Start timer
        int duration = plugin.getConfig().getInt("Utils3.Event.Duration", 600); // 10 minut default
        eventTimeLeft = duration;
        
        new BukkitRunnable() {
            private String currentEvent = eventName;
            
            @Override
            public void run() {
                eventTimeLeft--;
                
                // Update bossbars
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updateBossBar(player);
                }
                
                if (eventTimeLeft <= 0) {
                    if (currentGlobalEvent != null && currentGlobalEvent.equals(currentEvent)) {
                        stopGlobalEvent(eventData);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Co sekundę
    }

    private void eventMessage(String eventdata) {
        int duration = plugin.getConfig().getInt("Utils3.Event.Duration", 600);
        int minutes = duration / 60;
        
        String minutesPrefix = plugin.getConfig().getString("Utils3.EventName.MinutesPrefix", "min");
        String durText = minutes + " " + minutesPrefix;
        
        String event = plugin.getConfig().getString("Utils3.EventName." + eventdata, eventdata);
        
        String header = plugin.getConfig().getString("Utils3.Event.Header", "&6&l EVENT STARTED");
        header = ChatColor.translateAlternateColorCodes('&', header.replace("{EVENT}", event));
        
        String bottom = plugin.getConfig().getString("Utils3.Event.Bottom", "&7Duration: {TIME}");
        bottom = ChatColor.translateAlternateColorCodes('&', bottom.replace("{EVENT}", event).replace("{TIME}", durText));
        
        // Broadcast
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(header);
        Bukkit.broadcastMessage("");
        
        String line1 = plugin.getConfig().getString("Utils3.Event." + eventdata + ".1", "");
        String line2 = plugin.getConfig().getString("Utils3.Event." + eventdata + ".2", "");
        if (!line1.isEmpty()) Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', line1));
        if (!line2.isEmpty()) Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', line2));
        
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(bottom);
        Bukkit.broadcastMessage("");
        
        // Action bar
        String actionBar = plugin.getConfig().getString("Utils3.Event.StartActionBar", "&6EVENT: {EVENT} &7({TIME})");
        actionBar = ChatColor.translateAlternateColorCodes('&', actionBar.replace("{EVENT}", event).replace("{TIME}", durText));
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
                new net.md_5.bungee.api.chat.TextComponent(actionBar));
            
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
            
            // Create bossbar
            createBossBar(player);
        }
    }

    private void createBossBar(Player player) {
        if (currentGlobalEvent == null) return;
        
        BossBar bossBar = Bukkit.createBossBar(
            ChatColor.translateAlternateColorCodes('&', "&6Event: " + currentGlobalEvent),
            BarColor.YELLOW,
            BarStyle.SOLID
        );
        
        bossBar.addPlayer(player);
        playerBossBars.put(player.getUniqueId(), bossBar);
    }

    private void updateBossBar(Player player) {
        BossBar bossBar = playerBossBars.get(player.getUniqueId());
        if (bossBar == null) {
            createBossBar(player);
            bossBar = playerBossBars.get(player.getUniqueId());
        }
        
        if (bossBar != null && currentGlobalEvent != null) {
            int duration = plugin.getConfig().getInt("Utils3.Event.Duration", 600);
            double progress = (double) eventTimeLeft / duration;
            bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
            
            int minutes = eventTimeLeft / 60;
            int seconds = eventTimeLeft % 60;
            
            bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', 
                "&6Event: " + currentGlobalEvent + " &7(" + minutes + ":" + String.format("%02d", seconds) + ")"));
        }
    }

    private void stopGlobalEvent(String eventdata) {
        String event = plugin.getConfig().getString("Utils3.EventName." + eventdata, eventdata);
        
        Bukkit.broadcastMessage("");
        String mess = plugin.getConfig().getString("Utils3.Event.End", "&cEvent ended: {EVENT}");
        mess = ChatColor.translateAlternateColorCodes('&', mess.replace("{EVENT}", event));
        Bukkit.broadcastMessage(mess);
        Bukkit.broadcastMessage("");
        
        // Action bar
        String actionBar = plugin.getConfig().getString("Utils3.Event.EndActionBar", "&cEvent ended: {EVENT}");
        actionBar = ChatColor.translateAlternateColorCodes('&', actionBar.replace("{EVENT}", event));
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
                new net.md_5.bungee.api.chat.TextComponent(actionBar));
            
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.5f);
            
            // Remove bossbar
            BossBar bossBar = playerBossBars.remove(player.getUniqueId());
            if (bossBar != null) {
                bossBar.removeAll();
            }
        }
        
        currentGlobalEvent = null;
        eventTimeLeft = 0;
    }

    public void activateEvent(String type) {
        activeEvents.add(type);
    }

    public void stopEvent(String type) {
        activeEvents.remove(type);
    }

    public boolean isEventActive(String type) {
        return activeEvents.contains(type);
    }

    public double getJobExpMultiplier() {
        return isEventActive("DoubleXP") ? 2.0 : 1.0;
    }

    public double getJobMoneyMultiplier() {
        return isEventActive("JobBoost") ? 1.5 : 1.0;
    }
}
