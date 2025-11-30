package pl.nssv.skyblock.listeners;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.nssv.skyblock.SkyblocknNSSV;
public class PetEggListener implements Listener {
    private final SkyblocknNSSV plugin;
    public PetEggListener(SkyblocknNSSV plugin) { this.plugin = plugin; }
    @EventHandler
    public void onEggClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType() == Material.DRAGON_EGG) {
            p.sendMessage(ChatColor.GREEN + "Pet egg hatched!");
        }
    }
}
