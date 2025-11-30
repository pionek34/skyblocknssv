package pl.nssv.skyblock.listeners;

import org.bukkit.Material;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import pl.nssv.skyblock.SkyblocknNSSV;

public class ToolSkinListener implements Listener {
    private final SkyblocknNSSV plugin;

    public ToolSkinListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        
        Material tool = event.getPlayer().getInventory().getItemInMainHand().getType();
        String toolType = null;
        
        if (tool.name().endsWith("PICKAXE")) toolType = "Pickaxe";
        else if (tool.name().endsWith("AXE")) toolType = "Axe";
        else if (tool.name().endsWith("SHOVEL")) toolType = "Shovel";
        else if (tool.name().endsWith("HOE")) toolType = "Hoe";
        
        if (toolType != null) {
            plugin.getToolSkinManager().applySkinEffect(
                event.getPlayer(),
                event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                toolType
            );
        }
    }
}
