package pl.nssv.skyblock.managers;

import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.data.PlayerData;
import pl.nssv.skyblock.utils.ChatUtil;

public class MuteManager {
    
    private final SkyblocknNSSV plugin;
    
    public MuteManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    public void mutePlayer(Player player, long duration) {
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        data.setMuteTime(System.currentTimeMillis());
        data.setMuteDuration(duration);
    }
    
    public void unmutePlayer(Player player) {
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        data.unmute();
    }
    
    public boolean isMuted(Player player) {
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        return data.isMuted();
    }
    
    public long getRemainingMuteTime(Player player) {
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        return data.getRemainingMuteTime();
    }
    
    public String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + "d " + (hours % 24) + "h";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }
    
    public long parseTime(String timeString) {
        timeString = timeString.toLowerCase();
        
        long multiplier = 1000; // milisekundy
        int amount;
        
        if (timeString.endsWith("s")) {
            amount = Integer.parseInt(timeString.substring(0, timeString.length() - 1));
            return amount * multiplier;
        } else if (timeString.endsWith("m")) {
            amount = Integer.parseInt(timeString.substring(0, timeString.length() - 1));
            return amount * multiplier * 60;
        } else if (timeString.endsWith("h")) {
            amount = Integer.parseInt(timeString.substring(0, timeString.length() - 1));
            return amount * multiplier * 60 * 60;
        } else if (timeString.endsWith("d")) {
            amount = Integer.parseInt(timeString.substring(0, timeString.length() - 1));
            return amount * multiplier * 60 * 60 * 24;
        }
        
        return 0;
    }
}
