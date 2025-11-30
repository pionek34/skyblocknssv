package pl.nssv.skyblock.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.utils.ChatUtil;

public class ServerPingListener implements Listener {
    
    private final SkyblocknNSSV plugin;
    
    public ServerPingListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPing(ServerListPingEvent event) {
        String line1 = plugin.getConfig().getString("Global.Motd1");
        String line2 = plugin.getConfig().getString("Global.Motd2");
        
        String motd = ChatUtil.color(line1 + "\n" + line2);
        event.setMotd(motd);
    }
}
