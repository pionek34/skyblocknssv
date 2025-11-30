package pl.nssv.skyblock.listeners;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import pl.nssv.skyblock.SkyblocknNSSV;
public class PetInteractListener implements Listener {
    private final SkyblocknNSSV plugin;
    public PetInteractListener(SkyblocknNSSV plugin) { this.plugin = plugin; }
    @EventHandler
    public void onPetPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && 
            item.getItemMeta().getDisplayName().contains("Pet-Name")) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPetRightClick(PlayerInteractEvent e) {
        if (e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_AIR && 
            e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) return;
        if (item.getItemMeta().getDisplayName().contains("Lvl")) {
            plugin.getPetManager().addPet(p, item);
            item.setAmount(item.getAmount() - 1);
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.setWalkSpeed(0.2f);
        if (plugin.getPetManager().hasActivePet(p)) {
            plugin.getPetManager().spawnPet(p, false);
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (plugin.getPetManager().hasActivePet(e.getPlayer())) {
            plugin.getPetManager().despawnPet(e.getPlayer());
        }
    }
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        if (plugin.getPetManager().hasActivePet(e.getPlayer())) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> 
                plugin.getPetManager().spawnPet(e.getPlayer(), false), 1L);
        }
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (plugin.getPetManager().hasActivePet(e.getPlayer())) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> 
                plugin.getPetManager().spawnPet(e.getPlayer(), false), 10L);
        }
    }
}
