package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
import java.util.*;

public class ObjectiveManager {
    private final SkyblocknNSSV plugin;
    private final Map<UUID, String> objectives = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> questProgress = new HashMap<>();
    
    // Quest check lists z Skript
    private final List<String> questCheckList = Arrays.asList(
        "Collect4Logs", "Mine16Iron", "Mine8Gold", "Mine64Cobble", 
        "Mine128Cobble", "Mine16Diamond", "Mine64Ores"
    );
    
    private final List<Material> logTypes = Arrays.asList(
        Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG,
        Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG
    );
    
    private final List<Material> oreTypes = Arrays.asList(
        Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_REDSTONE_ORE,
        Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_LAPIS_ORE,
        Material.DEEPSLATE_EMERALD_ORE
    );
    
    private final List<String> newObjectives = Arrays.asList(
        "Craft16Planks", "CraftIronPickaxe", "CraftGoldenPickaxe",
        "ExpandIsland1", "UpgradeCobbleMinion2", "TalkWithMightyMiner2",
        "TalkWithMightyMiner3"
    );
    
    private final List<Integer> requiredAmounts = Arrays.asList(
        4, 16, 8, 64, 128, 16, 64
    );
    
    public ObjectiveManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    public void newObjective(Player player, String objective) {
        objectives.put(player.getUniqueId(), objective);
        
        // Send objective message
        String message = plugin.getConfig().getString("Structure.Objectives." + objective, 
            ChatColor.YELLOW + "New objective: " + objective);
        player.sendMessage(message);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
        
        // Special actions for specific objectives
        if (objective.equals("EnterPortal")) {
            plugin.getStructureManager().portalEffect(player);
        }
        
        if (objective.equals("BuildOverIsland")) {
            plugin.getStructureManager().portalEffect(player);
        }
        
        // Wait objectives - auto-convert on join
        if (objective.startsWith("Wait")) {
            // Will be converted on next join
        }
    }
    
    public String getObjective(Player player) {
        return objectives.getOrDefault(player.getUniqueId(), "");
    }
    
    public void questCheck(Player player, Material brokenBlock) {
        String currentObjective = getObjective(player);
        if (!questCheckList.contains(currentObjective)) return;
        
        int index = questCheckList.indexOf(currentObjective);
        if (index == -1) return;
        
        // Check if block matches quest
        boolean matches = false;
        
        switch (currentObjective) {
            case "Collect4Logs":
                matches = logTypes.contains(brokenBlock);
                break;
            case "Mine16Iron":
                matches = brokenBlock == Material.IRON_ORE;
                break;
            case "Mine8Gold":
                matches = brokenBlock == Material.GOLD_ORE;
                break;
            case "Mine64Cobble":
            case "Mine128Cobble":
                matches = brokenBlock == Material.COBBLESTONE;
                break;
            case "Mine16Diamond":
                matches = brokenBlock == Material.DEEPSLATE_DIAMOND_ORE;
                break;
            case "Mine64Ores":
                matches = oreTypes.contains(brokenBlock);
                break;
        }
        
        if (!matches) return;
        
        // Update progress
        UUID uuid = player.getUniqueId();
        questProgress.putIfAbsent(uuid, new HashMap<>());
        Map<String, Integer> playerQuests = questProgress.get(uuid);
        
        int current = playerQuests.getOrDefault(currentObjective, 0);
        current++;
        playerQuests.put(currentObjective, current);
        
        int required = requiredAmounts.get(index);
        
        if (current >= required) {
            // Complete quest
            String nextObjective = newObjectives.get(index);
            
            // Special actions
            if (currentObjective.equals("Mine16Iron")) {
                replaceItems(player, Material.IRON_ORE, Material.IRON_INGOT);
            }
            if (currentObjective.equals("Mine8Gold")) {
                replaceItems(player, Material.GOLD_ORE, Material.GOLD_INGOT);
            }
            
            newObjective(player, nextObjective);
            playerQuests.remove(currentObjective);
        } else {
            objectiveUpdate(player);
        }
    }
    
    public void objectiveUpdate(Player player) {
        String objective = getObjective(player);
        Map<String, Integer> playerQuests = questProgress.getOrDefault(player.getUniqueId(), new HashMap<>());
        
        int current = playerQuests.getOrDefault(objective, 0);
        int index = questCheckList.indexOf(objective);
        
        if (index >= 0 && index < requiredAmounts.size()) {
            int required = requiredAmounts.get(index);
            player.sendMessage(ChatColor.GRAY + "Progress: " + ChatColor.YELLOW + current + "/" + required);
        }
    }
    
    private void replaceItems(Player player, Material from, Material to) {
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < player.getInventory().getSize(); i++) {
                    org.bukkit.inventory.ItemStack item = player.getInventory().getItem(i);
                    if (item != null && item.getType() == from) {
                        item.setType(to);
                    }
                }
                player.updateInventory();
            }
        }.runTaskLater(plugin, 1L);
    }
    
    public void handlePlaceBlock(Player player) {
        String objective = getObjective(player);
        
        List<String> placeQuests = Arrays.asList("BuildOverIsland", "ExpandIsland1", "ExpandIsland2");
        List<Integer> placeReqs = Arrays.asList(12, 64, 128);
        List<String> placeNewObj = Arrays.asList("EnterPortal", "UpgradeIsland", "Mine128Cobble");
        
        int index = placeQuests.indexOf(objective);
        if (index == -1) return;
        
        UUID uuid = player.getUniqueId();
        questProgress.putIfAbsent(uuid, new HashMap<>());
        Map<String, Integer> playerQuests = questProgress.get(uuid);
        
        int current = playerQuests.getOrDefault(objective, 0);
        current++;
        playerQuests.put(objective, current);
        
        int required = placeReqs.get(index);
        
        if (current >= required) {
            if (objective.equals("ExpandIsland1")) {
                // Level up to 3 if needed
                plugin.getDataManager().getPlayerData(uuid).setLevel(3);
            }
            
            String nextObjective = placeNewObj.get(index);
            newObjective(player, nextObjective);
            playerQuests.remove(objective);
        }
    }
    
    public void handleCraft(Player player, org.bukkit.inventory.ItemStack item) {
        String objective = getObjective(player);
        
        if (objective.equals("Craft16Planks")) {
            newObjective(player, "BuildOverIsland");
        }
        
        if (objective.equals("CraftIronPickaxe") && item.getType() == Material.IRON_PICKAXE) {
            newObjective(player, "TalkWithMiner2");
        }
        
        if (objective.equals("CraftGoldenPickaxe") && item.getType() == Material.GOLDEN_PICKAXE) {
            newObjective(player, "TalkWithMiner3");
        }
    }
    
    public void handleJoin(Player player) {
        String objective = getObjective(player);
        
        // Convert Wait objectives
        Map<String, String> waitConversions = new HashMap<>();
        waitConversions.put("Wait0", "TalkWithJerry1");
        waitConversions.put("Wait3", "Mine16Iron");
        waitConversions.put("Wait4", "Mine8Gold");
        waitConversions.put("Wait5", "PlaceCobbleMinion");
        waitConversions.put("Wait6", "EnchantPickaxe1");
        waitConversions.put("Wait7", "ClaimKit");
        waitConversions.put("Wait8", "PlaceFarmerMinion");
        waitConversions.put("Wait9", "Mine16Diamond");
        waitConversions.put("Wait10", "TalkWithEnchanter2");
        waitConversions.put("Wait11", "PlaceDeepslateMinion");
        
        if (waitConversions.containsKey(objective)) {
            objectives.put(player.getUniqueId(), waitConversions.get(objective));
        }
    }
    
    public void clearProgress(Player player) {
        questProgress.remove(player.getUniqueId());
    }
    
    public int getProgress(Player player, String quest) {
        Map<String, Integer> playerQuests = questProgress.get(player.getUniqueId());
        if (playerQuests == null) return 0;
        return playerQuests.getOrDefault(quest, 0);
    }
}
