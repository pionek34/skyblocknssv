package pl.nssv.skyblock.listeners;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import pl.nssv.skyblock.SkyblocknNSSV;
public class PetGUIListener implements Listener {
    private final SkyblocknNSSV plugin;
    public PetGUIListener(SkyblocknNSSV plugin) { this.plugin = plugin; }
    @EventHandler
    public void onPetMenuClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        String title = ChatColor.stripColor(e.getView().getTitle());
        String expectedTitle = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',
            plugin.getConfig().getString("Update11.Pets.Default", "Pets")));
        if (!title.equals(expectedTitle)) return;
        e.setCancelled(true);
        if (e.getClickedInventory() == p.getInventory()) return;
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;
        int slot = e.getSlot();
        if (slot == 48 || slot == 49 || slot == 50) {
            p.closeInventory();
            return;
        }
        if (e.getClick() == ClickType.RIGHT) {
            ItemStack pet = clicked.clone();
            p.getInventory().addItem(pet);
            p.playSound(p.getLocation(), Sound.BLOCK_BEEHIVE_EXIT, 1.0f, 2.0f);
            p.sendMessage(ChatColor.GREEN + "Pet converted to item!");
            plugin.getPetManager().openPetMenu(p);
        } else {
            if (plugin.getPetManager().hasActivePet(p)) {
                ItemStack currentPet = plugin.getPetManager().getCurrentPet(p);
                if (currentPet != null && currentPet.isSimilar(clicked)) {
                    plugin.getPetManager().unsummonPet(p);
                    plugin.getPetManager().openPetMenu(p);
                    return;
                }
                p.sendMessage(ChatColor.RED + "You already have a pet summoned!");
                return;
            }
            plugin.getPetManager().summonPet(p, clicked);
            p.closeInventory();
        }
    }
}
