package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.data.PlayerData;
import java.util.*;

public class JobManager {
    private final SkyblocknNSSV plugin;
    private final List<String> allJobs = Arrays.asList(
        "Miner", "Builder", "Farmer", "Lumberjack", "Crafter",
        "Fisherman", "Hunter", "Enchanter", "Alchemist", "Digger"
    );

    public JobManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    public void addJobExp(Player player, String job, int amount) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        if (data == null) return;
        
        double multiplier = 1.0;
        if (player.hasPermission("jobs.multiplier.vip1")) multiplier = 1.25;
        if (player.hasPermission("jobs.multiplier.vip2")) multiplier = 1.5;
        if (player.hasPermission("jobs.multiplier.vip3")) multiplier = 1.75;
        if (player.hasPermission("jobs.multiplier.vip4")) multiplier = 2.0;
        
        int finalAmount = (int) (amount * multiplier);
        data.addJobExp(job, finalAmount);
        
        int level = data.getJobLevel(job);
        int exp = data.getJobExp(job);
        int required = level * 100;
        
        if (exp >= required && level < 100) {
            data.setJobLevel(job, level + 1);
            data.setJobExp(job, 0);
            player.sendMessage(ChatColor.GREEN + "Job " + job + " level up! Level: " + (level + 1));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
    }

    public List<String> getAllJobs() {
        return new ArrayList<>(allJobs);
    }

    public int getJobLevel(Player player, String job) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        return data != null ? data.getJobLevel(job) : 1;
    }
}
