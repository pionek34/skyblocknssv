package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.data.PlayerData;

import java.util.*;

public class PetManager {
    private final SkyblocknNSSV plugin;
    
    private final Map<UUID, ItemStack> currentPetItem = new HashMap<>();
    private final Map<UUID, Integer> currentPetID = new HashMap<>();
    private final Map<UUID, List<ItemStack>> playerPets = new HashMap<>();
    private final Map<UUID, UUID> petIconEntity = new HashMap<>();
    private final Map<UUID, UUID> petTextEntity = new HashMap<>();
    
    private final Map<String, ItemStack> petIcons = new HashMap<>();
    private final Map<String, Double> petExpMultiplier = new HashMap<>();
    private final Map<String, Double> petGemsMultiplier = new HashMap<>();
    private final List<String> petList = new ArrayList<>();
    
    private int petCustomID = 0;
    private ItemStack petEggIcon;

    public PetManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
        startPetTracking();
        loadDefaultPets();
    }

    private void loadDefaultPets() {
        petEggIcon = new ItemStack(Material.DRAGON_EGG);
    }

    public void openPetMenu(Player player) {
        String title = ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("Update11.Pets.Default", "&8Pets"));
        
        org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, 54, title);
        
        UUID uuid = player.getUniqueId();
        List<ItemStack> pets = playerPets.getOrDefault(uuid, new ArrayList<>());
        
        ItemStack currentPet = currentPetItem.get(uuid);
        if (currentPet != null) {
            pets.add(0, currentPet);
        }
        
        int slot = 0;
        for (ItemStack pet : pets) {
            if (slot >= 45) break;
            
            ItemStack displayPet = formatPet(pet.clone());
            ItemMeta meta = displayPet.getItemMeta();
            List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            
            lore.add("");
            int petID = getCustomID(pet);
            if (currentPetID.get(uuid) != null && currentPetID.get(uuid) == petID) {
                lore.addAll(plugin.getConfig().getStringList("Update11.Pets.Despawn"));
            } else {
                lore.addAll(plugin.getConfig().getStringList("Update11.Pets.Summon"));
                lore.add(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("Update11.Pets.Convert", "&cRight click to convert")));
            }
            
            meta.setLore(lore);
            displayPet.setItemMeta(meta);
            
            inv.setItem(slot++, displayPet);
        }
        
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Close");
        closeItem.setItemMeta(closeMeta);
        inv.setItem(48, closeItem);
        inv.setItem(49, closeItem);
        inv.setItem(50, closeItem);
        
        player.openInventory(inv);
    }

    public ItemStack formatPet(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return item;
        
        ItemMeta meta = item.getItemMeta();
        
        int level = getLevel(item);
        int rarity = getRarity(item);
        int exp = getExp(item);
        String petName = getPetName(item);
        
        String color = getColorByRarity(rarity);
        String rarityName = getRarityName(rarity);
        
        String displayName = plugin.getConfig().getString("Update11.PetFormat.FullName",
            "{COLOR}[Lvl {LEVEL}] {NAME}")
            .replace("{LEVEL}", String.valueOf(level))
            .replace("{COLOR}", color)
            .replace("{NAME}", petName);
        
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        
        List<String> lore = new ArrayList<>(plugin.getConfig().getStringList("Update11.PetFormat.Lore"));
        lore.add("");
        
        if (petExpMultiplier.containsKey(petName)) {
            double expMulti = petExpMultiplier.get(petName) * (1 + (level / 300.0)) * (1 + rarity / 40.0);
            String expLine = plugin.getConfig().getString("Update11.PetFormat.ExpMulti", "&bExp: +{EXP}%")
                .replace("{EXP}", String.format("%.1f", expMulti));
            lore.add(ChatColor.translateAlternateColorCodes('&', expLine));
            
            setNBTFloat(item, "Multi-Exp", (float) expMulti);
        }
        
        if (petGemsMultiplier.containsKey(petName)) {
            double gemsMulti = petGemsMultiplier.get(petName) * (1 + (level / 300.0)) * (1 + rarity / 40.0);
            String gemsLine = plugin.getConfig().getString("Update11.PetFormat.GemsMulti", "&bGems: +{GEMS}%")
                .replace("{GEMS}", String.format("%.1f", gemsMulti));
            lore.add(ChatColor.translateAlternateColorCodes('&', gemsLine));
            
            setNBTFloat(item, "Multi-Gems", (float) gemsMulti);
        }
        
        lore.add("");
        
        if (level >= 100) {
            lore.addAll(plugin.getConfig().getStringList("Update11.PetFormat.MaxBar"));
        } else {
            int required = level * 100;
            double percent = (exp / (double) required) * 100;
            String progressBar = createProgressBar(exp, required, 15);
            
            List<String> progressLines = plugin.getConfig().getStringList("Update11.PetFormat.Progress");
            for (String line : progressLines) {
                line = line.replace("{LEVEL}", String.valueOf(level + 1))
                    .replace("{PERCENT}", String.format("%.1f", percent))
                    .replace("{BAR}", progressBar)
                    .replace("{EXP}", String.valueOf(exp))
                    .replace("{REQ}", String.valueOf(required));
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
        
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("Update11.PetFormat.AddToMenu", "&eClick to add to menu")));
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', color + "&l" + rarityName));
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    private String createProgressBar(int current, int max, int length) {
        StringBuilder bar = new StringBuilder();
        for (int i = 1; i <= length; i++) {
            if (current >= (max / length) * i) {
                bar.append("&2&m ");
            } else {
                bar.append("&f&m ");
            }
        }
        
        if (current >= max) {
            return ChatColor.translateAlternateColorCodes('&', " &5&m" + bar.toString().replace("&", "") + "&r&5★");
        }
        return ChatColor.translateAlternateColorCodes('&', bar.toString() + "&r&f☆");
    }

    private String getColorByRarity(int rarity) {
        switch (rarity) {
            case 1: return "&f";
            case 2: return "&a";
            case 3: return "&9";
            case 4: return "&5";
            case 5: return "&6";
            case 6: return "&d";
            default: return "&7";
        }
    }

    private String getRarityName(int rarity) {
        switch (rarity) {
            case 1: return plugin.getConfig().getString("Update11.PetFormat.Common", "COMMON");
            case 2: return plugin.getConfig().getString("Update11.PetFormat.Uncommon", "UNCOMMON");
            case 3: return plugin.getConfig().getString("Update11.PetFormat.Rare", "RARE");
            case 4: return plugin.getConfig().getString("Update11.PetFormat.Epic", "EPIC");
            case 5: return plugin.getConfig().getString("Update11.PetFormat.Legendary", "LEGENDARY");
            case 6: return plugin.getConfig().getString("Update11.PetFormat.Exclusive", "EXCLUSIVE");
            default: return "UNKNOWN";
        }
    }

    public void spawnPet(Player player, boolean newLevel) {
        UUID uuid = player.getUniqueId();
        
        despawnPet(player);
        
        if (!currentPetID.containsKey(uuid)) return;
        
        Location playerLoc = player.getLocation();
        playerLoc.setPitch(0);
        
        Location petLoc = playerLoc.clone().add(playerLoc.getDirection().rotateAroundY(Math.PI / 2).multiply(1.75));
        petLoc.add(0, 1, 0);
        petLoc.setYaw(playerLoc.getYaw() + 180);
        
        ItemStack petItem = currentPetItem.get(uuid);
        if (petItem == null) return;
        
        ItemDisplay iconDisplay = (ItemDisplay) playerLoc.getWorld().spawnEntity(
            petLoc.clone().add(0, 0.8, 0), EntityType.ITEM_DISPLAY);
        iconDisplay.setItemStack(petItem);
        iconDisplay.setTransformation(new Transformation(
            new Vector3f(0, 0, 0),
            new org.joml.Quaternionf(),
            new Vector3f(1, 1, 1),
            new org.joml.Quaternionf()
        ));
        iconDisplay.setInterpolationDuration(4);
        
        petIconEntity.put(uuid, iconDisplay.getUniqueId());
        
        String displayName = petItem.getItemMeta().getDisplayName()
            .replace("Lvl ", "Lv")
            .replace("<##FF00FF>", "<##ff00fe>&l");
        
        TextDisplay textDisplay = (TextDisplay) playerLoc.getWorld().spawnEntity(
            petLoc.clone().add(0, 1, 0), EntityType.TEXT_DISPLAY);
        textDisplay.setText(displayName);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.setTransformation(new Transformation(
            new Vector3f(0, 0, 0),
            new org.joml.Quaternionf(),
            new Vector3f(1, 1, 1),
            new org.joml.Quaternionf()
        ));
        textDisplay.setInterpolationDuration(4);
        
        petTextEntity.put(uuid, textDisplay.getUniqueId());
        
        if (newLevel) {
            int level = getLevel(petItem);
            // Level up effects
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
            player.getWorld().spawnParticle(Particle.FIREWORK, petLoc, 30, 0.3, 0.3, 0.3, 0.1);
        }
    }

    public void despawnPet(Player player) {
        UUID uuid = player.getUniqueId();
        
        UUID iconUUID = petIconEntity.remove(uuid);
        UUID textUUID = petTextEntity.remove(uuid);
        
        if (iconUUID != null) {
            Entity icon = Bukkit.getEntity(iconUUID);
            if (icon != null) icon.remove();
        }
        
        if (textUUID != null) {
            Entity text = Bukkit.getEntity(textUUID);
            if (text != null) text.remove();
        }
    }

    public void addPetExp(Player player) {
        UUID uuid = player.getUniqueId();
        ItemStack petItem = currentPetItem.get(uuid);
        
        if (petItem == null) return;
        
        int level = getLevel(petItem);
        if (level >= 100) return;
        
        int exp = getExp(petItem) + 3;
        int required = level * 100;
        
        boolean leveledUp = false;
        
        if (exp >= required) {
            level++;
            exp = 0;
            leveledUp = true;
            
            String petName = getPetName(petItem);
            String message = plugin.getConfig().getString("Update11.PetLevelup", "&aPet leveled up!")
                .replace("{PET}", petName)
                .replace("{LEVEL}", String.valueOf(level));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
        }
        
        setNBTInt(petItem, "Pet-Exp", exp);
        setNBTInt(petItem, "Pet-Level", level);
        
        currentPetItem.put(uuid, formatPet(petItem));
        
        if (leveledUp) {
            spawnPet(player, true);
        }
    }

    private void startPetTracking() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    
                    if (!currentPetID.containsKey(uuid)) continue;
                    
                    if (!petIconEntity.containsKey(uuid)) {
                        spawnPet(player, false);
                        continue;
                    }
                    
                    UUID iconUUID = petIconEntity.get(uuid);
                    UUID textUUID = petTextEntity.get(uuid);
                    
                    Entity icon = Bukkit.getEntity(iconUUID);
                    Entity text = Bukkit.getEntity(textUUID);
                    
                    if (icon == null) {
                        spawnPet(player, false);
                        continue;
                    }
                    
                    Location playerLoc = player.getLocation();
                    playerLoc.setPitch(0);
                    
                    Location petLoc = playerLoc.clone().add(
                        playerLoc.getDirection().rotateAroundY(Math.PI / 2).multiply(1.75));
                    petLoc.add(0, 1, 0);
                    petLoc.setYaw(playerLoc.getYaw() + 180);
                    
                    if (text != null) {
                        text.teleport(petLoc.clone().add(0, 1, 0));
                    }
                    
                    icon.teleport(petLoc.clone().add(0, 0.8, 0));
                    
                    petParticle(petLoc);
                }
            }
        }.runTaskTimer(plugin, 0L, 4L);
    }

    private void petParticle(Location loc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                loc.getWorld().spawnParticle(Particle.CRIT, loc.clone().add(0, 0.3, 0), 1, 0, 0, 0, 0);
            }
        }.runTaskLater(plugin, 4L);
    }

    public void givePet(Player player, String petName, int level, int rarity) {
        if (!petIcons.containsKey(petName)) {
            player.sendMessage(ChatColor.RED + "Pet doesn't exist!");
            return;
        }
        
        ItemStack pet = petIcons.get(petName).clone();
        
        setNBTString(pet, "Pet-Name", petName);
        setNBTInt(pet, "Pet-Level", level);
        setNBTInt(pet, "Pet-Exp", 0);
        setNBTInt(pet, "Pet-Rarity", rarity);
        setNBTInt(pet, "Pet-CustomID", ++petCustomID);
        
        player.getInventory().addItem(formatPet(pet));
    }

    public void addPet(Player player, ItemStack pet) {
        UUID uuid = player.getUniqueId();
        
        List<ItemStack> pets = playerPets.computeIfAbsent(uuid, k -> new ArrayList<>());
        
        if (currentPetItem.get(uuid) != null) {
            if (pets.size() >= 27) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("Update11.PetLimit", "&cPet limit reached!")));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 2.0f);
                return;
            }
        } else if (pets.size() >= 28) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("Update11.PetLimit", "&cPet limit reached!")));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 2.0f);
            return;
        }
        
        setNBTInt(pet, "Pet-CustomID", ++petCustomID);
        pets.add(pet);
        
        String message = plugin.getConfig().getString("Update11.PetAddedtoMenu", "&aPet added to menu!")
            .replace("{NAME}", getPetName(pet));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
    }

    public void summonPet(Player player, ItemStack pet) {
        UUID uuid = player.getUniqueId();
        
        if (currentPetID.containsKey(uuid)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("Update11.AlreadyActive", "&cYou already have a pet active!")));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 2.0f);
            return;
        }
        
        int petID = getCustomID(pet);
        currentPetID.put(uuid, petID);
        currentPetItem.put(uuid, formatPet(pet.clone()));
        
        List<ItemStack> pets = playerPets.get(uuid);
        if (pets != null) {
            pets.removeIf(p -> getCustomID(p) == petID);
        }
        
        String message = plugin.getConfig().getString("Update11.Summoned", "&aSummoned pet!")
            .replace("{NAME}", getPetName(pet));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        player.playSound(player.getLocation(), Sound.BLOCK_BEEHIVE_EXIT, 1.0f, 2.0f);
        player.playSound(player.getLocation(), Sound.UI_TOAST_IN, 2.0f, 2.0f);
        
        spawnPet(player, false);
    }

    public void unsummonPet(Player player) {
        UUID uuid = player.getUniqueId();
        
        ItemStack petItem = currentPetItem.remove(uuid);
        Integer petID = currentPetID.remove(uuid);
        
        if (petItem != null) {
            List<ItemStack> pets = playerPets.computeIfAbsent(uuid, k -> new ArrayList<>());
            pets.add(petItem);
            
            despawnPet(player);
            
            String message = plugin.getConfig().getString("Update11.Despawn", "&cDespawned pet!")
                .replace("{NAME}", getPetName(petItem));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            player.playSound(player.getLocation(), Sound.BLOCK_BEEHIVE_EXIT, 1.0f, 1.5f);
        }
    }

    private String getPetName(ItemStack item) {
        return getNBTString(item, "Pet-Name");
    }

    private int getLevel(ItemStack item) {
        return getNBTInt(item, "Pet-Level");
    }

    private int getExp(ItemStack item) {
        return getNBTInt(item, "Pet-Exp");
    }

    private int getRarity(ItemStack item) {
        return getNBTInt(item, "Pet-Rarity");
    }

    private int getCustomID(ItemStack item) {
        return getNBTInt(item, "Pet-CustomID");
    }

    private void setNBTString(ItemStack item, String key, String value) {
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(value.hashCode());
        item.setItemMeta(meta);
    }

    private String getNBTString(ItemStack item, String key) {
        return item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? 
            item.getItemMeta().getDisplayName() : "";
    }

    private void setNBTInt(ItemStack item, String key, int value) {
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(value);
        item.setItemMeta(meta);
    }

    private int getNBTInt(ItemStack item, String key) {
        return item.hasItemMeta() && item.getItemMeta().hasCustomModelData() ? 
            item.getItemMeta().getCustomModelData() : 0;
    }

    private void setNBTFloat(ItemStack item, String key, float value) {
        // Stored in lore or meta
    }

    public Map<String, ItemStack> getPetIcons() { return petIcons; }
    public Map<String, Double> getPetExpMultiplier() { return petExpMultiplier; }
    public Map<String, Double> getPetGemsMultiplier() { return petGemsMultiplier; }
    public List<String> getPetList() { return petList; }
    public ItemStack getPetEggIcon() { return petEggIcon; }
    public void setPetEggIcon(ItemStack icon) { this.petEggIcon = icon; }
    
    public boolean hasActivePet(Player player) {
        return currentPetID.containsKey(player.getUniqueId());
    }
    
    public ItemStack getCurrentPet(Player player) {
        return currentPetItem.get(player.getUniqueId());
    }
}
