package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nssv.skyblock.SkyblocknNSSV;
import java.util.*;

public class FishingManager {
    private final SkyblocknNSSV plugin;
    private final Map<UUID, FishingData> activeFishing = new HashMap<>();

    public FishingManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    public void startFishing(Player player, ItemStack reward) {
        Random random = new Random();
        int length = random.nextInt(5) + 3;
        StringBuilder sequence = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sequence.append(random.nextBoolean() ? "L" : "R");
        }
        
        FishingData data = new FishingData(sequence.toString(), reward);
        activeFishing.put(player.getUniqueId(), data);
        
        player.sendTitle(ChatColor.GOLD + "FISHING!", ChatColor.YELLOW + sequence.toString(), 5, 30, 5);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (activeFishing.containsKey(player.getUniqueId())) {
                    cancelFishing(player);
                }
            }
        }.runTaskLater(plugin, 30L);
    }

    public void addInput(Player player, String input) {
        FishingData data = activeFishing.get(player.getUniqueId());
        if (data == null) return;
        
        data.playerInput += input;
        
        if (data.playerInput.length() >= data.sequence.length()) {
            boolean success = data.playerInput.equals(data.sequence);
            finishFishing(player, success, data.reward);
        }
    }

    private void finishFishing(Player player, boolean success, ItemStack reward) {
        activeFishing.remove(player.getUniqueId());
        
        if (success) {
            player.getInventory().addItem(reward);
            player.sendMessage(ChatColor.GREEN + "Success! You caught a fish!");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            plugin.getJobManager().addJobExp(player, "Fisherman", 25);
        } else {
            player.sendMessage(ChatColor.RED + "Failed! Fish got away!");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 0.5f);
        }
    }

    public void cancelFishing(Player player) {
        activeFishing.remove(player.getUniqueId());
        player.sendMessage(ChatColor.RED + "Fishing cancelled!");
    }

    public boolean isFishing(Player player) {
        return activeFishing.containsKey(player.getUniqueId());
    }

    public ItemStack getRandomFish() {
        Random random = new Random();
        int roll = random.nextInt(100);
        
        if (roll < 40) return new ItemStack(Material.COD);
        if (roll < 70) return new ItemStack(Material.SALMON);
        if (roll < 90) return new ItemStack(Material.TROPICAL_FISH);
        return new ItemStack(Material.PUFFERFISH);
    }

    private static class FishingData {
        String sequence;
        String playerInput = "";
        ItemStack reward;
        
        FishingData(String sequence, ItemStack reward) {
            this.sequence = sequence;
            this.reward = reward;
        }
    }
}
