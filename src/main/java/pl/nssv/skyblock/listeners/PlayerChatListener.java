package pl.nssv.skyblock.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.data.PlayerData;
import pl.nssv.skyblock.utils.ChatUtil;

public class PlayerChatListener implements Listener {
    
    private final SkyblocknNSSV plugin;
    
    public PlayerChatListener(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        PlayerData data = plugin.getDataManager().getPlayerData(event.getPlayer());
        
        // Sprawdz chatgame
        if (plugin.getChatGameManager().checkAnswer(event.getPlayer(), event.getMessage())) {
            event.setCancelled(true);
            return;
        }
        
        // Sprawdz mute
        if (data.isMuted()) {
            event.setCancelled(true);
            
            String timeFormatted = plugin.getMuteManager().formatTime(data.getRemainingMuteTime());
            String msg = plugin.getConfig().getString("Utils.Mute.CurrentlyMuted");
            msg = msg.replace("{TIME}", timeFormatted);
            event.getPlayer().sendMessage(ChatUtil.color(msg));
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 2.0f);
            return;
        }
        
        // Format czatu
        String chatFormat = plugin.getConfig().getString("Global.ChatFormat");
        String message = event.getMessage();
        
        // Sprawdz czy moze kolorować
        if (!event.getPlayer().hasPermission("ChatFormat.Colored")) {
            message = ChatUtil.stripColor(message);
        }
        
        // Sprawdz emoji
        if (event.getPlayer().hasPermission("ChatFormat.Emojis")) {
            message = message.replace(":tableflip:", "(╯°□°)╯︵ ┻━┻");
            message = message.replace(":shrug:", "¯\\_(͡❛ ͜ʖ ͡❛)_/¯");
            message = message.replace(":lenny:", "(͡❛ ͜ʖ ͡❛)");
            message = message.replace(":yes:", "✔");
            message = message.replace(":no:", "❌");
            message = message.replace("<3", "❤");
        }
        
        // Pobierz dane gracza
        int level = data.getLevel();
        
        // Pobierz prefix z LuckPerms jesli dostepny
        String rank = "";
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            rank = PlaceholderAPI.setPlaceholders(event.getPlayer(), "%luckperms_prefix%");
        }
        
        // Icon
        String icon = data.getPrefixIcon();
        String iconColor = data.getPrefixColor();
        if (iconColor != null && !iconColor.isEmpty()) {
            icon = iconColor + icon;
        }
        
        // Nick z gradientem
        String nick = event.getPlayer().getName();
        String nickData = data.getChatNickname();
        if (nickData != null && !nickData.isEmpty()) {
            String[] colors = nickData.split(" \\|!\\| ");
            if (colors.length == 2) {
                nick = ChatUtil.formatGradient(nick, colors[0], colors[1]);
                if (data.isChatBold()) {
                    nick = "§l" + nick;
                }
            }
        }
        
        // Message z gradientem
        String messageData = data.getChatMessage();
        if (messageData != null && !messageData.isEmpty()) {
            String[] colors = messageData.split(" \\|!\\| ");
            if (colors.length == 2) {
                if (message.length() == 1) {
                    message = "<##" + colors[0] + ">" + message;
                } else {
                    message = ChatUtil.formatGradient(message, colors[0], colors[1]);
                }
            }
        }
        
        // Clan
        String clan = "";
        String clanName = data.getClan();
        if (clanName != null && !clanName.isEmpty()) {
            // Pobierz kolor klanu jesli ustawiony
            String clanColor = plugin.getConfig().getString("Clans." + clanName + ".Color", "&c");
            clan = clanColor + "[" + clanName + "] ";
        }
        
        // Island level z PlaceholderAPI
        String islandLevel = "0";
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            islandLevel = PlaceholderAPI.setPlaceholders(event.getPlayer(), "%Level_bskyblock_island_level%");
        }
        
        // Zastap wszystko
        chatFormat = chatFormat.replace("{LEVEL}", "<##fbbb26>✯" + level);
        chatFormat = chatFormat.replace("{ISLEVEL}", "<##fbbb26>✯" + islandLevel);
        chatFormat = chatFormat.replace("{ICON}", "<##545454>" + icon);
        chatFormat = chatFormat.replace("{CLAN}", clan);
        chatFormat = chatFormat.replace("{RANK}", "&f" + rank);
        chatFormat = chatFormat.replace("{NICK}", "<##d0d0d0>" + nick);
        chatFormat = chatFormat.replace("{MESSAGE}", "<##bdbebe>" + message);
        
        event.setFormat(ChatUtil.color(chatFormat));
    }
}
