package pl.nssv.skyblock.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.nssv.skyblock.SkyblocknNSSV;

public class SkyblockPlaceholders extends PlaceholderExpansion {
    private final SkyblocknNSSV plugin;
    
    public SkyblockPlaceholders(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public @NotNull String getIdentifier() {
        return "skyblock";
    }
    
    @Override
    public @NotNull String getAuthor() {
        return "pvsky";
    }
    
    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";
        
        switch (identifier.toLowerCase()) {
            case "tab":
                return player.getWorld().getName().equals("world") ? "false" : "true";
                
            case "playernick":
                return player.getName();
                
            case "glowcolor":
                return "&f";
                
            case "level":
                return String.valueOf(plugin.getDataManager().getPlayerData(player.getUniqueId()).getLevel());
                
            case "is_level":
                return "0";
                
            case "clan":
                return "None";
                
            case "clan_formatted":
                return "";
                
            case "gems":
                return String.valueOf(plugin.getDataManager().getPlayerData(player.getUniqueId()).getGems());
                
            case "job":
                return "None";
                
            case "mining_miner":
            case "mining_lucky":
            case "mining_ores":
                return "0";
                
            case "dungeons":
                return "&cDungeons Closed";
                
            case "playtime":
                return formatPlaytime(player);
                
            default:
                return null;
        }
    }
    
    private String formatPlaytime(Player player) {
        long ticks = player.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE);
        long seconds = ticks / 20;
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
}
