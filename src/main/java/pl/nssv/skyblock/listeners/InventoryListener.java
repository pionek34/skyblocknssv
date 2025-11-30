package pl.nssv.skyblock.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.data.PlayerData;
import pl.nssv.skyblock.gui.JobGUI;
import pl.nssv.skyblock.utils.ChatUtil;

public class InventoryListener implements Listener {
    
    private final SkyblocknNSSV plugin;
    
    public InventoryListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getView().getTitle() == null) return;
        
        String title = ChatUtil.stripColor(event.getView().getTitle());
        
        // Sprawdz czy to GUI prac (pusty tytul)
        if (!title.equals("") && !title.isEmpty()) return;
        
        event.setCancelled(true);
        
        // Sprawdz czy kliknal w swoje inventory
        if (event.getClickedInventory() == event.getWhoClicked().getInventory()) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        if (clicked == null || !clicked.hasItemMeta()) return;
        
        int slot = event.getSlot();
        
        // Przycisk Next Page (slot 0)
        if (slot == 0) {
            int page = getPageFromItem(clicked);
            new JobGUI(plugin, player, page - 1).open();
            return;
        }
        
        // Przycisk Prev Page (slot 8)
        if (slot == 8) {
            int page = getPageFromItem(clicked);
            new JobGUI(plugin, player, page + 1).open();
            return;
        }
        
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        String currentJob = data.getCurrentJob();
        
        // Przyciski Close (slot 37, 38, 39 gdy ma prace LUB 39, 40, 41 gdy nie ma)
        if (currentJob != null) {
            if (slot == 37 || slot == 38 || slot == 39) {
                player.closeInventory();
                return;
            }
            
            // Przyciski Leave (slot 41, 42, 43)
            if (slot == 41 || slot == 42 || slot == 43) {
                //String jobName = plugin.getJobManager().getJobDisplayName(currentJob);
                String msg = plugin.getConfig().getString("Utils.JobLeft");
                msg = msg.replace("{JOB}", "przerobic");
                player.sendMessage(ChatUtil.color(msg));
                
                data.setCurrentJob(null);
                new JobGUI(plugin, player, 0).open();
                return;
            }
        } else {
            if (slot == 39 || slot == 40 || slot == 41) {
                player.closeInventory();
                return;
            }
        }
        
        // Sloty z pracami (1-7)
        if (slot >= 1 && slot <= 7) {
            String job = getJobFromItem(clicked);
            
            if (job == null || job.equals("NoAccess")) {
                player.sendMessage(ChatUtil.color(plugin.getConfig().getString("Utils.JobLocked")));
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 2.0f);
                return;
            }
            
            // Sprawdz czy juz ma ta prace
            if (job.equals(currentJob)) {
                //String jobName = plugin.getJobManager().getJobDisplayName(job);
                String msg = plugin.getConfig().getString("Utils.AlreadyJob");
                msg = msg.replace("{JOB}", "przerobic");
                player.sendMessage(ChatUtil.color(msg));
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 2.0f);
                return;
            }
            
            // Sprawdz delay na zmiane pracy
            long changeTime = data.getJobChangeTime();
            if (changeTime > 0) {
                long elapsed = (System.currentTimeMillis() - changeTime) / 1000;
                if (elapsed < 10) {
                    String msg = plugin.getConfig().getString("Utils.JobDelay");
                    msg = msg.replace("{SECONDS}", String.valueOf(10 - elapsed));
                    player.sendMessage(ChatUtil.color(msg));
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 2.0f);
                    return;
                }
            }
            
            // Ustaw nowa prace
            data.setCurrentJob(job);
            data.setJobChangeTime(System.currentTimeMillis());
            
            //String jobName = plugin.getJobManager().getJobDisplayName(job);
            String msg = plugin.getConfig().getString("Utils.JobSelected");
            msg = msg.replace("{JOB}", "przerobic");
            player.sendMessage(ChatUtil.color(msg));
            
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.6f, 2.0f);
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.6f, 2.0f);
            
            new JobGUI(plugin, player, 0).open();
        }
    }
    
    private int getPageFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        
        NamespacedKey key = new NamespacedKey(plugin, "page");
        if (item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        }
        
        return 0;
    }
    
    private String getJobFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        
        NamespacedKey key = new NamespacedKey(plugin, "job");
        if (item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        }
        
        return null;
    }
}
