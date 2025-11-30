package pl.nssv.skyblock.managers;
import org.bukkit.*;
import org.bukkit.entity.Player;
import pl.nssv.skyblock.SkyblocknNSSV;
import pl.nssv.skyblock.data.PlayerData;
import java.util.*;
public class ClanManager {
    private final SkyblocknNSSV plugin;
    private final Map<String, ClanData> clans = new HashMap<>();
    private final Map<UUID, String> playerClans = new HashMap<>();
    private final Map<UUID, String> clanInvites = new HashMap<>();
    public ClanManager(SkyblocknNSSV plugin) { this.plugin = plugin; }
    public void sendHelp(Player p) {
        p.sendMessage("");
        for (String line : plugin.getConfig().getStringList("Utils3.ClanHelp")) 
            p.sendMessage(line.replace("&", "§"));
        p.sendMessage("");
    }
    public void createClan(Player p, String tag) {
        PlayerData data = plugin.getDataManager().getPlayerData(p);
        if (data.getLevel() < 15) { p.sendMessage("§cMusisz mieć poziom 15!"); return; }
        if (tag == null) { p.sendMessage("§cPodaj tag klanu!"); return; }
        if (playerClans.containsKey(p.getUniqueId())) { p.sendMessage("§cJuż jesteś w klanie!"); return; }
        if (tag.length() < 3 || tag.length() > 4) { p.sendMessage("§cTag musi mieć 3-4 znaki!"); return; }
        if (clans.containsKey(tag.toUpperCase())) { p.sendMessage("§cTen tag już istnieje!"); return; }
        ClanData clan = new ClanData(tag.toUpperCase(), p.getUniqueId());
        clans.put(tag.toUpperCase(), clan);
        playerClans.put(p.getUniqueId(), tag.toUpperCase());
        p.sendMessage("§aPomyślnie utworzono klan §e" + tag.toUpperCase());
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);
    }
    public void deleteClan(Player p) {
        if (!playerClans.containsKey(p.getUniqueId())) { p.sendMessage("§cNie jesteś w klanie!"); return; }
        String tag = playerClans.get(p.getUniqueId());
        ClanData clan = clans.get(tag);
        if (!clan.getLeader().equals(p.getUniqueId())) { p.sendMessage("§cNie jesteś liderem!"); return; }
        p.sendMessage("§aUsunięto klan!");
        for (UUID uuid : clan.getMembers()) {
            playerClans.remove(uuid);
            Player m = Bukkit.getPlayer(uuid);
            if (m != null) m.sendMessage("§cKlan został usunięty przez §e" + p.getName());
        }
        clans.remove(tag);
        playerClans.remove(p.getUniqueId());
    }
    public void inviteClan(Player p, String name) {
        if (name == null) { p.sendMessage("§cPodaj gracza!"); return; }
        if (!playerClans.containsKey(p.getUniqueId())) { p.sendMessage("§cNie jesteś w klanie!"); return; }
        String tag = playerClans.get(p.getUniqueId());
        ClanData clan = clans.get(tag);
        if (!clan.getLeader().equals(p.getUniqueId())) { p.sendMessage("§cNie jesteś liderem!"); return; }
        Player t = Bukkit.getPlayer(name);
        if (t == null) { p.sendMessage("§cGracz offline!"); return; }
        if (playerClans.containsKey(t.getUniqueId())) { p.sendMessage("§cGracz jest w klanie!"); return; }
        if (clan.getMembers().size() >= 8) { p.sendMessage("§cLimit czlonkow!"); return; }
        clanInvites.put(t.getUniqueId(), tag);
        p.sendMessage("§aZaproszono gracza §e" + name);
        t.sendMessage("§aZaproszenie do klanu §e" + tag + " §7(/clan join " + tag + ")");
        t.playSound(t.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
    }
    public void joinClan(Player p, String tag) {
        if (tag == null) { p.sendMessage("§cPodaj tag!"); return; }
        if (playerClans.containsKey(p.getUniqueId())) { p.sendMessage("§cJuż jesteś w klanie!"); return; }
        if (!clanInvites.containsKey(p.getUniqueId()) || !clanInvites.get(p.getUniqueId()).equalsIgnoreCase(tag)) {
            p.sendMessage("§cBrak zaproszenia!"); return;
        }
        ClanData clan = clans.get(tag.toUpperCase());
        if (clan == null) { p.sendMessage("§cKlan nie istnieje!"); return; }
        clan.addMember(p.getUniqueId());
        playerClans.put(p.getUniqueId(), tag.toUpperCase());
        clanInvites.remove(p.getUniqueId());
        p.sendMessage("§aDołączono do klanu §e" + tag);
        for (UUID uuid : clan.getMembers()) {
            Player m = Bukkit.getPlayer(uuid);
            if (m != null && !m.equals(p)) m.sendMessage("§aGracz §e" + p.getName() + " §adołączył!");
        }
    }
    public void leaveClan(Player p) {
        if (!playerClans.containsKey(p.getUniqueId())) { p.sendMessage("§cNie jesteś w klanie!"); return; }
        String tag = playerClans.get(p.getUniqueId());
        ClanData clan = clans.get(tag);
        if (clan.getLeader().equals(p.getUniqueId())) { p.sendMessage("§cLider nie może opuścić klanu!"); return; }
        clan.removeMember(p.getUniqueId());
        playerClans.remove(p.getUniqueId());
        p.sendMessage("§cOpuściłeś klan §e" + tag);
    }
    public void kickClan(Player p, String name) {
        if (name == null) { p.sendMessage("§cPodaj gracza!"); return; }
        if (!playerClans.containsKey(p.getUniqueId())) { p.sendMessage("§cNie jesteś w klanie!"); return; }
        String tag = playerClans.get(p.getUniqueId());
        ClanData clan = clans.get(tag);
        if (!clan.getLeader().equals(p.getUniqueId())) { p.sendMessage("§cNie jesteś liderem!"); return; }
        OfflinePlayer t = Bukkit.getOfflinePlayer(name);
        if (!clan.getMembers().contains(t.getUniqueId())) { p.sendMessage("§cGracz nie jest w klanie!"); return; }
        clan.removeMember(t.getUniqueId());
        playerClans.remove(t.getUniqueId());
        p.sendMessage("§cWyrzucono gracza §e" + name);
        if (t.isOnline()) ((Player)t).sendMessage("§cZostałeś wyrzucony z klanu!");
    }
    public void infoClan(Player p, String tag) {
        String t = tag != null ? tag : playerClans.get(p.getUniqueId());
        if (t == null) { p.sendMessage("§cPodaj tag!"); return; }
        ClanData clan = clans.get(t.toUpperCase());
        if (clan == null) { p.sendMessage("§cKlan nie istnieje!"); return; }
        p.sendMessage("§6§lINFO KLANU");
        p.sendMessage("§7Tag: §e" + clan.getTag());
        p.sendMessage("§7Lider: §e" + Bukkit.getOfflinePlayer(clan.getLeader()).getName());
        p.sendMessage("§7Czlonkowie: §e" + (clan.getMembers().size() + 1));
    }
    public void colorClan(Player p) {
        p.sendMessage("§7Funkcja w budowie");
    }
    public String getPlayerClan(UUID uuid) { return playerClans.get(uuid); }
    public ClanData getClan(String tag) { return clans.get(tag != null ? tag.toUpperCase() : null); }
    
    public void addClanExp(Player player, int amount) {
        String tag = playerClans.get(player.getUniqueId());
        if (tag == null) return;
        
        ClanData clan = clans.get(tag);
        if (clan != null) {
            clan.addExp(amount);
        }
    }
    
    public void clanInfo(Player p, String tag) {
        String t = tag != null ? tag : playerClans.get(p.getUniqueId());
        if (t == null) { p.sendMessage("§cPodaj tag!"); return; }
        
        ClanData clan = clans.get(t.toUpperCase());
        if (clan == null) { p.sendMessage("§cKlan nie istnieje!"); return; }
        
        p.sendMessage("");
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lCLAN INFO"));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Tag: &e" + clan.getTag()));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Level: &e" + clan.getLevel()));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Exp: &e" + clan.getExp()));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Leader: &e" + Bukkit.getOfflinePlayer(clan.getLeader()).getName()));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Members: &e" + (clan.getMembers().size())));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Max Members: &e" + clan.getMaxMembers()));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Cave Bonus: &e+" + ((int)((clan.getCaveBonusMultiplier() - 1) * 100)) + "%"));
        p.sendMessage("");
    }
    
    public static class ClanData {
        private final String tag;
        private final UUID leader;
        private final Set<UUID> members = new HashSet<>();
        private int exp = 0;
        private int level = 1;
        
        public ClanData(String tag, UUID leader) { this.tag = tag; this.leader = leader; }
        public String getTag() { return tag; }
        public UUID getLeader() { return leader; }
        public Set<UUID> getMembers() { return members; }
        public void addMember(UUID uuid) { members.add(uuid); }
        public void removeMember(UUID uuid) { members.remove(uuid); }
        
        public int getExp() { return exp; }
        public int getLevel() { return level; }
        public void setExp(int exp) { this.exp = exp; }
        public void setLevel(int level) { this.level = level; }
        
        public void addExp(int amount) {
            this.exp += amount;
            checkLevelUp();
        }
        
        private void checkLevelUp() {
            int requiredExp = 100 + (level * 50);
            if (exp >= requiredExp) {
                exp -= requiredExp;
                level++;
                for (UUID uuid : members) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&a&lCLAN LEVEL UP! &e" + tag + " &7is now level &e" + level));
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.5f);
                    }
                }
            }
        }
        
        public double getCaveBonusMultiplier() {
            return 1.0 + (level * 0.02);
        }
        
        public int getMaxMembers() {
            return 5 + level;
        }
    }
}