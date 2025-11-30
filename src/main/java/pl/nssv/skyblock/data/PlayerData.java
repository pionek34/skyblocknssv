package pl.nssv.skyblock.data;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class PlayerData {
    
    private final UUID uuid;
    private final FileConfiguration config;
    
    public PlayerData(UUID uuid, FileConfiguration config) {
        this.uuid = uuid;
        this.config = config;
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    // Job data
    public String getCurrentJob() {
        return config.getString("job.current");
    }
    
    public void setCurrentJob(String job) {
        config.set("job.current", job);
    }
    
    public int getJobLevel(String job) {
        return config.getInt("job." + job + ".level", 1);
    }
    
    public void setJobLevel(String job, int level) {
        config.set("job." + job + ".level", level);
    }
    
    public int getJobExp(String job) {
        return config.getInt("job." + job + ".exp", 0);
    }
    
    public void setJobExp(String job, int exp) {
        config.set("job." + job + ".exp", exp);
    }
    
    public void addJobExp(String job, int exp) {
        setJobExp(job, getJobExp(job) + exp);
    }
    
    public long getJobChangeTime() {
        return config.getLong("job.changetime", 0);
    }
    
    public void setJobChangeTime(long time) {
        config.set("job.changetime", time);
    }
    
    // Level data
    public int getLevel() {
        return config.getInt("level", 0);
    }
    
    public void setLevel(int level) {
        config.set("level", level);
    }
    
    public void addLevel(int amount) {
        setLevel(getLevel() + amount);
    }
    
    // Experience data
    public int getExp() {
        return config.getInt("exp", 0);
    }
    
    public void setExp(int exp) {
        config.set("exp", exp);
    }
    
    public void addExp(int amount) {
        setExp(getExp() + amount);
    }
    
    // Gems data
    public int getGems() {
        return config.getInt("gems", 0);
    }
    
    public void setGems(int gems) {
        config.set("gems", gems);
    }
    
    public void addGems(int amount) {
        setGems(getGems() + amount);
    }
    
    // Mute data
    public long getMuteTime() {
        return config.getLong("mute.time", 0);
    }
    
    public void setMuteTime(long time) {
        config.set("mute.time", time);
    }
    
    public long getMuteDuration() {
        return config.getLong("mute.duration", 0);
    }
    
    public void setMuteDuration(long duration) {
        config.set("mute.duration", duration);
    }
    
    public boolean isMuted() {
        if (getMuteTime() == 0) return false;
        long elapsed = System.currentTimeMillis() - getMuteTime();
        return elapsed < getMuteDuration();
    }
    
    public void unmute() {
        config.set("mute.time", null);
        config.set("mute.duration", null);
    }
    
    public long getRemainingMuteTime() {
        if (!isMuted()) return 0;
        long elapsed = System.currentTimeMillis() - getMuteTime();
        return getMuteDuration() - elapsed;
    }
    
    // Immunity data
    public boolean hasImmunity() {
        return config.getBoolean("immunity", false);
    }
    
    public void setImmunity(boolean immunity) {
        config.set("immunity", immunity);
    }
    
    // Chat customization
    public String getChatNickname() {
        return config.getString("chat.nickname");
    }
    
    public void setChatNickname(String nickname) {
        config.set("chat.nickname", nickname);
    }
    
    public String getChatMessage() {
        return config.getString("chat.message");
    }
    
    public void setChatMessage(String message) {
        config.set("chat.message", message);
    }
    
    public boolean isChatBold() {
        return config.getBoolean("chat.bold", false);
    }
    
    public void setChatBold(boolean bold) {
        config.set("chat.bold", bold);
    }
    
    public String getPrefixIcon() {
        return config.getString("prefix.icon", "×");
    }
    
    public void setPrefixIcon(String icon) {
        config.set("prefix.icon", icon);
    }
    
    public String getPrefixColor() {
        return config.getString("prefix.color");
    }
    
    public void setPrefixColor(String color) {
        config.set("prefix.color", color);
    }
    
    // Clan data
    public String getClan() {
        return config.getString("clan");
    }
    
    public void setClan(String clan) {
        config.set("clan", clan);
    }
    
    // Nickname color (Utils4)
    public String getNicknameColor() {
        return config.getString("chat.nicknameColor", "&7");
    }
    
    public void setNicknameColor(String color) {
        config.set("chat.nicknameColor", color);
    }
    
    // Message color (Utils4)
    public String getMessageColor() {
        return config.getString("chat.messageColor", "&f");
    }
    
    public void setMessageColor(String color) {
        config.set("chat.messageColor", color);
    }
    
    // Gamma (Utils4)
    public boolean hasGamma() {
        return config.getBoolean("gamma", false);
    }
    
    public void setGamma(boolean gamma) {
        config.set("gamma", gamma);
    }
}
