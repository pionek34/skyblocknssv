package pl.nssv.skyblock.managers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.data.PlayerData;
import pl.nssv.skyblock.utils.ChatUtil;

import java.util.Random;

public class ChatGameManager {
    
    private final SkyblocknNSSV plugin;
    private final Random random = new Random();
    private Economy economy;
    
    private int num1;
    private int num2;
    private String operator;
    private int answer;
    private boolean gameActive = false;
    
    public ChatGameManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
        setupEconomy();
    }
    
    private void setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        economy = rsp.getProvider();
    }
    
    public void startChatGame() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    startGame();
                }
            }
        }.runTaskTimer(plugin, 20 * 60 * 10, 20 * 60 * 10); // Co 10 minut
    }
    
    public void startGame() {
        int type = random.nextInt(3) + 1;
        String spacefix = "";
        
        switch (type) {
            case 1: // Dodawanie
                num1 = random.nextInt(945) + 55; // 55-999
                num2 = random.nextInt(945) + 55;
                operator = "+";
                answer = num1 + num2;
                break;
            case 2: // Odejmowanie
                num1 = random.nextInt(945) + 55;
                num2 = random.nextInt(945) + 55;
                operator = "-";
                answer = num1 - num2;
                break;
            case 3: // Mnozenie
                num1 = random.nextInt(13) + 3; // 3-15
                num2 = random.nextInt(13) + 3;
                operator = "*";
                answer = num1 * num2;
                spacefix = "  ";
                break;
        }
        
        gameActive = true;
        
        // Wyslij wiadomosc do wszystkich
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("");
            player.sendMessage(ChatUtil.color(plugin.getConfig().getString("Utils.ChatGameTitle")));
            player.sendMessage("");
            player.sendMessage(ChatUtil.color(plugin.getConfig().getString("Utils.ChatGameDesc")));
            player.sendMessage("");
            
            String question = plugin.getConfig().getString("Utils.ChatGameAsk");
            question = question.replace("{SPACEFIX}", spacefix);
            question = question.replace("{NUM1}", String.valueOf(num1));
            question = question.replace("{SYMBOL}", operator);
            question = question.replace("{NUM2}", String.valueOf(num2));
            player.sendMessage(ChatUtil.color(question));
            
            player.sendMessage("");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.5f);
        }
        
        // Timer na 30 sekund
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameActive) {
                    gameActive = false;
                    
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage("");
                        player.sendMessage(ChatUtil.color(plugin.getConfig().getString("Utils.NobodyAnswer")));
                        player.sendMessage("");
                        
                        // Odtworz smutny dzwiek
                        playSadSound(player);
                    }
                }
            }
        }.runTaskLater(plugin, 20 * 30); // 30 sekund
    }
    
    private void playSadSound(Player player) {
        float pitch = 2.0f;
        for (int i = 0; i < 5; i++) {
            final float finalPitch = pitch;
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, finalPitch);
                }
            }.runTaskLater(plugin, i * 6L); // 0.3 sekundy = 6 tickow
            pitch -= 0.2f;
        }
    }
    
    public boolean checkAnswer(Player player, String message) {
        if (!gameActive) return false;
        
        try {
            int playerAnswer = Integer.parseInt(message);
            if (playerAnswer == answer) {
                gameActive = false;
                
                // Nagroda
                int rewardMoney = random.nextInt(351) + 150; // 150-500
                int rewardGems = random.nextInt(8) + 3; // 3-10
                
                if (economy != null) {
                    economy.depositPlayer(player, rewardMoney);
                }
                
                PlayerData data = plugin.getDataManager().getPlayerData(player);
                data.addGems(rewardGems);
                
                // Wiadomosci
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage("");
                    String msg = plugin.getConfig().getString("Utils.GoodAnswer");
                    msg = msg.replace("{PLAYER}", player.getName());
                    p.sendMessage(ChatUtil.color(msg));
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.4f);
                }
                
                player.sendMessage("");
                String msg = plugin.getConfig().getString("Utils.ChatGameRew");
                msg = msg.replace("{MONEY}", String.valueOf(rewardMoney));
                msg = msg.replace("{SHARDS}", String.valueOf(rewardGems));
                player.sendMessage(ChatUtil.color(msg));
                
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage("");
                }
                
                return true;
            }
        } catch (NumberFormatException ignored) {
        }
        
        return false;
    }
    
    public boolean isGameActive() {
        return gameActive;
    }
}
