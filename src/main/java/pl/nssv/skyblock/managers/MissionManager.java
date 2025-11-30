package pl.nssv.skyblock.managers;
import org.bukkit.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
import java.util.*;
public class MissionManager {
    private final SkyblocknNSSV plugin;
    public MissionManager(SkyblocknNSSV plugin) { this.plugin = plugin; }
    
    // Overloaded checkMission for simple type checking
    public void checkMission(Player player, int missionType) {
        // Mission type: 2=Place, 4=Craft, 5=Consume, 6=Enchant, 7=Kill
        // Stub implementation - to be expanded based on actual mission system
    }
    
    public void checkMission(Player player, String type, Material material, int amount) {}
    
    public void openMissionsGUI(Player player) {
        player.openInventory(Bukkit.createInventory(null, 27, "Missions"));
    }
}
