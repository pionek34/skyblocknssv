package pl.nssv.skyblock.managers;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
public class ChatManager {
    private final SkyblocknNSSV plugin;
    public ChatManager(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public void openNickGUI(Player player) {
        player.sendMessage("§7GUI customizacji czatu (w budowie)");
    }
}
