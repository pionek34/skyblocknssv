package pl.nssv.skyblock.gui;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.nssv.skyblock.SkyblocknNSSV;
import java.util.*;
public class JobGUI {
    private final SkyblocknNSSV plugin;
    private final Player player;
    public JobGUI(SkyblocknNSSV plugin, Player player, int page) {
        this.plugin = plugin;
        this.player = player;
    }
    public void open() {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "Jobs");
        List<String> jobs = plugin.getJobManager().getAllJobs();
        for (int i = 0; i < jobs.size(); i++) {
            ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + jobs.get(i));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        player.openInventory(inv);
    }
}
