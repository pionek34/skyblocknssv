package pl.nssv.skyblock.listeners;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.clip.placeholderapi.PlaceholderAPI;
import pl.nssv.skyblock.SkyblocknNSSV;
import java.util.*;
public class PortalListener implements Listener {
    private final SkyblocknNSSV plugin;
    private final Map<UUID, Location> tempPortalLocs = new HashMap<>();
    public PortalListener(SkyblocknNSSV plugin) { this.plugin = plugin; }
    @EventHandler
    public void onPortalBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.NETHER_PORTAL) return;
        e.setCancelled(true);
        openRemovePortalGUI(e.getPlayer(), e.getBlock().getLocation());
    }
    @EventHandler
    public void onPortalClick(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null || e.getClickedBlock().getType() != Material.NETHER_PORTAL) return;
        for (int i = 1; i <= 5; i++) {
            Location above = e.getClickedBlock().getLocation().add(0, i, 0);
            if (above.getBlock().getType() == Material.NETHER_PORTAL) continue;
            if (above.getBlock().getType() == Material.OBSIDIAN) return;
            openRemovePortalGUI(e.getPlayer(), e.getClickedBlock().getLocation());
            return;
        }
    }
    private void openRemovePortalGUI(Player p, Location loc) {
        if (!p.getWorld().getName().equals("bskyblock_world")) return;
        String owner = PlaceholderAPI.setPlaceholders(p, "%bskyblock_island_owner%");
        if (!owner.equals(p.getName())) {
            p.sendMessage(ChatColor.RED + "Only island owner can remove portals!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 2.0f);
            return;
        }
        tempPortalLocs.put(p.getUniqueId(), loc);
        org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, 27, ChatColor.RED + "Remove Portal?");
        ItemStack confirm = new ItemStack(Material.LIME_WOOL);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "Confirm");
        confirm.setItemMeta(confirmMeta);
        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED + "Cancel");
        cancel.setItemMeta(cancelMeta);
        inv.setItem(10, confirm);
        inv.setItem(11, confirm);
        inv.setItem(12, confirm);
        inv.setItem(14, cancel);
        inv.setItem(15, cancel);
        inv.setItem(16, cancel);
        p.openInventory(inv);
    }
    @EventHandler
    public void onPortalGUIClick(org.bukkit.event.inventory.InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        if (!ChatColor.stripColor(e.getView().getTitle()).equals("Remove Portal?")) return;
        e.setCancelled(true);
        if (e.getClickedInventory() == p.getInventory()) return;
        int slot = e.getSlot();
        if (slot == 10 || slot == 11 || slot == 12) {
            Location portalLoc = tempPortalLocs.remove(p.getUniqueId());
            if (portalLoc != null) {
                portalLoc.getBlock().setType(Material.AIR);
                p.sendMessage(ChatColor.GREEN + "Portal removed!");
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
            }
            p.closeInventory();
        } else if (slot == 14 || slot == 15 || slot == 16) {
            tempPortalLocs.remove(p.getUniqueId());
            p.closeInventory();
        }
    }
    @EventHandler
    public void onPortalGUIClose(org.bukkit.event.inventory.InventoryCloseEvent e) {
        if (ChatColor.stripColor(e.getView().getTitle()).equals("Remove Portal?")) {
            tempPortalLocs.remove(e.getPlayer().getUniqueId());
        }
    }
}
