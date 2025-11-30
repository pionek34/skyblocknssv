package pl.nssv.skyblock.managers;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.data.PlayerData;

public class LevelManager {
    private final SkyblocknNSSV plugin;
    
    public LevelManager(SkyblocknNSSV plugin) {
        this.plugin = plugin;
    }
    
    public void addExp(Player player, int exp, boolean hide) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        
        if (data.getLevel() >= 100) return;
        
        // Double XP event
        // if (globalEvent is DoubleXP) exp *= 2;
        
        // Pet multiplier
        // exp *= petMultiplier;
        
        data.addExp(exp);
        
        int required = data.getLevel() * 250;
        if (required == 0 || required == 250) required = 50;
        
        if (data.getExp() >= required) {
            data.setExp(0);
            data.setLevel(data.getLevel() + 1);
            levelUp(player);
        } else if (!hide) {
            sendExpActionBar(player, data.getExp(), required, exp);
        }
    }
    
    private void sendExpActionBar(Player player, int current, int required, int gained) {
        String message = ChatColor.GREEN + "Level " + plugin.getDataManager().getPlayerData(player.getUniqueId()).getLevel() + 
                        ChatColor.AQUA + " " + current + "/" + required + " xp  " +
                        ChatColor.GREEN + "+" + gained + " xp";
        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
            new net.md_5.bungee.api.chat.TextComponent(message));
    }
    
    public void levelUp(Player player) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        int level = data.getLevel();
        
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        
        // Give rewards
        giveLevelRewards(player, level);
        
        // Send messages
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "LEVEL UP!");
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "You are now Level " + level + "!");
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "Rewards:");
        
        // Show benefits
        showLevelBenefits(player, level);
        
        player.sendMessage("");
        
        // Animations
        levelUpAnimation(player, level);
    }
    
    private void giveLevelRewards(Player player, int level) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        
        // Money reward
        int money = 500 * level;
        if (level > 10) money = 5000 + 250 * (level - 10);
        if (level > 20) money = 7500 + 125 * (level - 20);
        
        if (plugin.getEconomy() != null) {
            plugin.getEconomy().depositPlayer(player, money);
        }
        
        // Gems reward
        int gems = 25;
        if (level % 10 == 0) gems = 250;
        else if (level % 5 == 0) gems = 50;
        
        data.addGems(gems);
        
        // Crate keys
        if (level % 50 == 0) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crates key give " + player.getName() + " skyblock 1");
        } else if (level % 30 == 0) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crates key give " + player.getName() + " legendary 1");
        } else if (level % 15 == 0) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crates key give " + player.getName() + " minions 1");
        } else if (level % 10 == 0) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crates key give " + player.getName() + " magical 1");
        } else if (level >= 2) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crates key give " + player.getName() + " common 2");
        }
        
        // Fly at level 50
        if (level == 50) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set essentials.fly");
        }
    }
    
    private void showLevelBenefits(Player player, int level) {
        player.sendMessage(ChatColor.YELLOW + "  ○ " + ChatColor.GOLD + "Money");
        player.sendMessage(ChatColor.YELLOW + "  ○ " + ChatColor.GOLD + "Gems");
        
        if (level >= 2) {
            player.sendMessage(ChatColor.YELLOW + "  ○ " + ChatColor.GOLD + "Crate Keys");
        }
        
        if (level % 3 == 0 && level <= 15) {
            player.sendMessage(ChatColor.YELLOW + "  ○ " + ChatColor.GOLD + "Extra Home");
        }
        
        if (level >= 25 && level % 5 == 0) {
            player.sendMessage(ChatColor.YELLOW + "  ○ " + ChatColor.GOLD + "New Color");
        }
        
        if (level == 50) {
            player.sendMessage(ChatColor.YELLOW + "  ○ " + ChatColor.GOLD + "/fly Permission");
        }
    }
    
    private void levelUpAnimation(Player player, int level) {
        Location loc = player.getLocation().add(0, 2.5, 0);
        
        // Spawn text display
        TextDisplay display = (TextDisplay) player.getWorld().spawnEntity(loc, EntityType.TEXT_DISPLAY);
        display.setText(ChatColor.YELLOW + "" + ChatColor.BOLD + "LEVEL " + level);
        display.setBillboard(Display.Billboard.CENTER);
        
        // Bounce animation
        new BukkitRunnable() {
            int ticks = 0;
            double[] bounceIn = {0, 0.1, 0.25, 0.45, 0.7, 0.9, 1.02, 1.08, 1.1, 1.09, 1.07, 1.05, 1.03, 1.01, 1};
            
            @Override
            public void run() {
                if (ticks < bounceIn.length) {
                    double size = bounceIn[ticks] * 1.2;
                    // Set display scale
                    ticks++;
                } else if (ticks < bounceIn.length + 40) {
                    // Hold
                    ticks++;
                } else if (ticks < bounceIn.length + 40 + bounceIn.length) {
                    // Fade out
                    int fadeIndex = ticks - bounceIn.length - 40;
                    if (fadeIndex < bounceIn.length) {
                        double size = 1.2 + bounceIn[fadeIndex];
                        int opacity = 255 - (fadeIndex * (255 / bounceIn.length));
                        // Set opacity
                    }
                    ticks++;
                } else {
                    display.remove();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        // Confetti
        spawnConfetti(player);
    }
    
    private void spawnConfetti(Player player) {
        new BukkitRunnable() {
            int count = 0;
            
            @Override
            public void run() {
                if (count >= 80) {
                    cancel();
                    return;
                }
                
                Location loc = player.getLocation().add(
                    Math.random() * 2.6 - 1.3,
                    3 + Math.random() * 0.6 - 0.3,
                    Math.random() * 2.6 - 1.3
                );
                
                player.getWorld().spawnParticle(Particle.FIREWORK, loc, 1);
                count++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
