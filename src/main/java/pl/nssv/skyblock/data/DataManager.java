package pl.nssv.skyblock.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {
    
    private final SkyblocknNSSV plugin;
    private final File dataFolder;
    private final Map<UUID, PlayerData> playerDataMap;
    private File clanFile;
    private FileConfiguration clanConfig;
    
    public DataManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        this.playerDataMap = new HashMap<>();
        
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        // Inicjalizacja clan config
        clanFile = new File(plugin.getDataFolder(), "clans.yml");
        if (!clanFile.exists()) {
            try {
                clanFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        clanConfig = YamlConfiguration.loadConfiguration(clanFile);
    }
    
    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }
    
    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, k -> loadPlayerData(uuid));
    }
    
    private PlayerData loadPlayerData(UUID uuid) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return new PlayerData(uuid, config);
    }
    
    public void savePlayerData(Player player) {
        savePlayerData(player.getUniqueId());
    }
    
    public void savePlayerData(UUID uuid) {
        PlayerData data = playerDataMap.get(uuid);
        if (data != null) {
            File file = new File(dataFolder, uuid.toString() + ".yml");
            try {
                data.getConfig().save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Nie mozna zapisac danych gracza: " + uuid);
                e.printStackTrace();
            }
        }
    }
    
    public void saveAll() {
        for (UUID uuid : playerDataMap.keySet()) {
            savePlayerData(uuid);
        }
    }
    
    public void unloadPlayerData(UUID uuid) {
        savePlayerData(uuid);
        playerDataMap.remove(uuid);
    }
    
    public FileConfiguration getClanConfig() {
        return clanConfig;
    }
    
    public void saveClanConfig() {
        try {
            clanConfig.save(clanFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Nie mozna zapisac danych klanow!");
            e.printStackTrace();
        }
    }
}
