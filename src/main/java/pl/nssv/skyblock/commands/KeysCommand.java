package pl.nssv.skyblock.commands;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.utils.ChatUtil;
public class KeysCommand implements CommandExecutor {
    private final SkyblocknNSSV plugin;
    public KeysCommand(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        player.sendMessage("");
        String msg = plugin.getConfig().getString("Utils2.ClickKeys");
        msg = msg.replace("{LINK}", plugin.getConfig().getString("Global.StoreLink"));
        player.sendMessage(ChatUtil.color(msg));
        player.sendMessage("");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
        return true;
    }
}
