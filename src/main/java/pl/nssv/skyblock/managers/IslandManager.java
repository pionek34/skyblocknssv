package pl.nssv.skyblock.managers;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
import java.util.*;
public class IslandManager {
    private final SkyblocknNSSV plugin;
    private final Map<UUID, Map<String, Integer>> upgrades = new HashMap<>();
    public IslandManager(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public int getUpgrade(UUID owner, String type) {
        return upgrades.computeIfAbsent(owner, k -> new HashMap<>()).getOrDefault(type, 0);
    }
    public void setUpgrade(UUID owner, String type, int level) {
        upgrades.computeIfAbsent(owner, k -> new HashMap<>()).put(type, level);
    }
}
