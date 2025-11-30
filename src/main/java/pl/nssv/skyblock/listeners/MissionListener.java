package pl.nssv.skyblock.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import pl.nssv.skyblock.SkyblocknNSSV;

public class MissionListener implements Listener {
    private final SkyblocknNSSV plugin;

    public MissionListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        // Type 2 - Place Blocks
        plugin.getMissionManager().checkMission(player, 2);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        // Type 4 - Craft Items
        plugin.getMissionManager().checkMission(player, 4);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        // Type 5 - Consume Food
        plugin.getMissionManager().checkMission(player, 5);
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getEnchanter();
        // Type 6 - Enchant Items
        plugin.getMissionManager().checkMission(player, 6);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        
        // Type 7 - Kill Mobs
        plugin.getMissionManager().checkMission(killer, 7);
    }
}
