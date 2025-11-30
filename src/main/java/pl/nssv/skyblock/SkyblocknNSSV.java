package pl.nssv.skyblock;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.nssv.skyblock.commands.*;
import pl.nssv.skyblock.data.DataManager;
import pl.nssv.skyblock.listeners.*;
import pl.nssv.skyblock.managers.*;

public class SkyblocknNSSV extends JavaPlugin {
    
    private static SkyblocknNSSV instance;
    private DataManager dataManager;
    private JobManager jobManager;
    private HologramManager hologramManager;
    private ChatGameManager chatGameManager;
    private MuteManager muteManager;
    private VoteManager voteManager;
    private CoinflipManager coinflipManager;
    private HomeManager homeManager;
    private EffectsManager effectsManager;
    private QuestManager questManager;
    private ClanManager clanManager;
    private EventManager eventManager;
    private IslandManager islandManager;
    private LevelManager levelManager;
    private ChatManager chatManager;
    private BoostManager boostManager;
    
    // Nowe managery z Utils3 i Utils4
    private FishingManager fishingManager;
    private MissionManager missionManager;
    private GlobalFlyManager globalFlyManager;
    private AFKManager afkManager;
    private ToolSkinManager toolSkinManager;
    private PrefixIconManager prefixIconManager;
    private NickColorsManager nickColorsManager;
    private IslandAnimationManager islandAnimationManager;
    
    // Nowe managery z Utils5
    private PetManager petManager;
    
    // Nowe managery z Structure.sk
    private StructureManager structureManager;
    private ObjectiveManager objectiveManager;
    private BankManager bankManager;
    private WarpManager warpManager;
    
    // Nowe managery z Skyblock.sk i Minions.sk
    private MinionManager minionManager;
    private SkyblockPlaceholders skyblockPlaceholders;
    private SpawnCommand spawnCommand;
    private CaveManager caveManager;
    
    // Dungeons.sk i ColorUtils.sk
    private DungeonManager dungeonManager;
    
    // Cinematic.sk i BossbarStats.sk
    private CinematicManager cinematicManager;
    private BossbarStatsManager bossbarStatsManager;
    
    // Vault Economy i PlaceholderAPI
    private net.milkbowl.vault.economy.Economy economy;
    private me.clip.placeholderapi.PlaceholderAPI placeholderAPI;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Zapisz domyslny config
        saveDefaultConfig();
        
        // Inicjalizacja managerow
        dataManager = new DataManager(this);
        jobManager = new JobManager(this);
        hologramManager = new HologramManager(this);
        chatGameManager = new ChatGameManager(this);
        muteManager = new MuteManager(this);
        voteManager = new VoteManager(this);
        coinflipManager = new CoinflipManager(this);
        homeManager = new HomeManager(this);
        effectsManager = new EffectsManager(this);
        questManager = new QuestManager(this);
        clanManager = new ClanManager(this);
        eventManager = new EventManager(this);
        islandManager = new IslandManager(this);
        levelManager = new LevelManager(this);
        chatManager = new ChatManager(this);
        boostManager = new BoostManager(this);
        
        // Nowe managery
        fishingManager = new FishingManager(this);
        missionManager = new MissionManager(this);
        globalFlyManager = new GlobalFlyManager(this);
        afkManager = new AFKManager(this);
        toolSkinManager = new ToolSkinManager(this);
        prefixIconManager = new PrefixIconManager(this);
        nickColorsManager = new NickColorsManager(this);
        islandAnimationManager = new IslandAnimationManager(this);
        
        // Utils5 managery
        petManager = new PetManager(this);
        
        // Structure.sk managery
        objectiveManager = new ObjectiveManager(this);
        structureManager = new StructureManager(this);
        bankManager = new BankManager(this);
        warpManager = new WarpManager(this);
        
        // Skyblock.sk i Minions.sk managery
        minionManager = new MinionManager(this);
        spawnCommand = new SpawnCommand(this);
        caveManager = new CaveManager(this);
        levelManager = new LevelManager(this);
        
        // Dungeons.sk managery
        dungeonManager = new DungeonManager(this);
        
        // Cinematic.sk i BossbarStats.sk managery
        cinematicManager = new CinematicManager(this);
        bossbarStatsManager = new BossbarStatsManager(this);
        
        // Setup economy
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            economy = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
        }
        
        // Register PlaceholderAPI expansion
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            skyblockPlaceholders = new SkyblockPlaceholders(this);
            skyblockPlaceholders.register();
        }
        
        // Rejestracja komend
        registerCommands();
        
        // Rejestracja listenerow
        registerListeners();
        
        // Uruchomienie hologramu
        Bukkit.getScheduler().runTaskLater(this, () -> {
            hologramManager.loadHologram();
        }, 20L);
        
        // Uruchomienie chatgame
        chatGameManager.startChatGame();
        
        getLogger().info("SkyblocknNSSV zostal wlaczony!");
    }
    
    @Override
    public void onDisable() {
        // Zapisz dane
        if (dataManager != null) {
            dataManager.saveAll();
        }
        
        // Wyczysc hologramy
        if (hologramManager != null) {
            hologramManager.cleanup();
        }
        
        getLogger().info("SkyblocknNSSV zostal wylaczony!");
    }
    
    private void registerCommands() {
        getCommand("xqeffects").setExecutor(new XqEffectsCommand(this));
        getCommand("emoji").setExecutor(new EmojiCommand(this));
        getCommand("job").setExecutor(new JobCommand(this));
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("unmute").setExecutor(new UnmuteCommand(this));
        getCommand("chatgame").setExecutor(new ChatGameCommand(this));
        getCommand("vote").setExecutor(new VoteCommand(this));
        getCommand("discord").setExecutor(new DiscordCommand(this));
        getCommand("keys").setExecutor(new KeysCommand(this));
        getCommand("votereward").setExecutor(new VoteRewardCommand(this));
        getCommand("coinflip").setExecutor(new CoinflipCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("delhome").setExecutor(new DelHomeCommand(this));
        getCommand("helpop").setExecutor(new HelpOpCommand(this));
        getCommand("gamma").setExecutor(new GammaCommand(this));
        getCommand("gperm").setExecutor(new GPermCommand(this));
        getCommand("staffchat").setExecutor(new StaffChatCommand(this));
        getCommand("quests").setExecutor(new QuestsCommand(this));
        getCommand("clan").setExecutor(new ClanCommand(this));
        getCommand("event").setExecutor(new EventCommand(this));
        getCommand("upgrades").setExecutor(new UpgradesCommand(this));
        getCommand("kit").setExecutor(new KitCommand(this));
        getCommand("kits").setExecutor(new KitsCommand(this));
        getCommand("rank").setExecutor(new RankCommand(this));
        getCommand("store").setExecutor(new StoreCommand(this));
        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("level").setExecutor(new LevelCommand(this));
        getCommand("nick").setExecutor(new NickCommand(this));
        getCommand("top").setExecutor(new TopCommand(this));
        
        // Nowe komendy z Utils3/Utils4
        getCommand("daily").setExecutor(new DailyCommand(this));
        getCommand("globalfly").setExecutor(new GlobalFlyCommand(this));
        getCommand("afk").setExecutor(new AFKCommand(this));
        getCommand("toolskins").setExecutor(new ToolSkinsCommand(this));
        getCommand("icon").setExecutor(new IconCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(this));
        
        // Nowe komendy z Utils5
        getCommand("pet").setExecutor(new PetCommand(this));
        getCommand("adminpet").setExecutor(new AdminPetCommand(this));
        
        // Komendy Structure.sk
        StructureCommands structureCmds = new StructureCommands(this);
        getCommand("detectislandcreate").setExecutor(structureCmds);
        getCommand("genisland").setExecutor(structureCmds);
        getCommand("setwarp").setExecutor(structureCmds);
        getCommand("delwarp").setExecutor(structureCmds);
        getCommand("warp").setExecutor(structureCmds);
        getCommand("warps").setExecutor(structureCmds);
        getCommand("bank").setExecutor(structureCmds);
        getCommand("isbank").setExecutor(structureCmds);
        getCommand("help").setExecutor(structureCmds);
        getCommand("panel").setExecutor(structureCmds);
        getCommand("islandpanel").setExecutor(structureCmds);
        getCommand("ispanel").setExecutor(structureCmds);
        
        // Komendy Skyblock.sk i Minions.sk
        getCommand("spawn").setExecutor(spawnCommand);
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("minionsadmin").setExecutor(new MinionAdminCommand(this));
        
        // Komendy Dungeons.sk
        getCommand("dungeon").setExecutor(new DungeonCommand(this));
        getCommand("dungeonadmin").setExecutor(new DungeonAdminCommand(this));
        
        // Komendy Cinematic.sk i BossbarStats.sk
        getCommand("cinematic").setExecutor(new CinematicCommand(this));
        getCommand("tutorial").setExecutor(new TutorialCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerPingListener(this), this);
        getServer().getPluginManager().registerEvents(new JobListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerWelcomeListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new UpgradeGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new LevelGUIListener(this), this);
        
        // Nowe listenery z Utils3/Utils4
        getServer().getPluginManager().registerEvents(new FishingListener(this), this);
        getServer().getPluginManager().registerEvents(new MissionListener(this), this);
        getServer().getPluginManager().registerEvents(new MissionGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new GlobalFlyListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerHopperListener(this), this);
        getServer().getPluginManager().registerEvents(new ToolSkinListener(this), this);
        getServer().getPluginManager().registerEvents(new MagicFeatherListener(this), this);
        getServer().getPluginManager().registerEvents(new AllGUIListeners(this), this);
        
        // Nowe listenery z Utils5
        getServer().getPluginManager().registerEvents(new PetGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new PetInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new PetEggListener(this), this);
        getServer().getPluginManager().registerEvents(new PortalListener(this), this);
        
        // Listenery Structure.sk
        getServer().getPluginManager().registerEvents(new StructureListener(this), this);
    }
    
    public static SkyblocknNSSV getInstance() {
        return instance;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
    
    public JobManager getJobManager() {
        return jobManager;
    }
    
    public HologramManager getHologramManager() {
        return hologramManager;
    }
    
    public ChatGameManager getChatGameManager() {
        return chatGameManager;
    }
    
    public MuteManager getMuteManager() {
        return muteManager;
    }
    
    public VoteManager getVoteManager() {
        return voteManager;
    }
    
    public CoinflipManager getCoinflipManager() {
        return coinflipManager;
    }
    
    public HomeManager getHomeManager() {
        return homeManager;
    }
    
    public EffectsManager getEffectsManager() {
        return effectsManager;
    }
    
    public QuestManager getQuestManager() {
        return questManager;
    }
    
    public ClanManager getClanManager() {
        return clanManager;
    }
    
    public EventManager getEventManager() {
        return eventManager;
    }
    
    public IslandManager getIslandManager() {
        return islandManager;
    }
    
    public LevelManager getLevelManager() {
        return levelManager;
    }
    
    public ChatManager getChatManager() {
        return chatManager;
    }
    
    public BoostManager getBoostManager() {
        return boostManager;
    }
    
    // Gettery dla nowych managerów
    public FishingManager getFishingManager() {
        return fishingManager;
    }
    
    public MissionManager getMissionManager() {
        return missionManager;
    }
    
    public GlobalFlyManager getGlobalFlyManager() {
        return globalFlyManager;
    }
    
    public AFKManager getAFKManager() {
        return afkManager;
    }
    
    public ToolSkinManager getToolSkinManager() {
        return toolSkinManager;
    }
    
    public PrefixIconManager getPrefixIconManager() {
        return prefixIconManager;
    }
    
    public NickColorsManager getNickColorsManager() {
        return nickColorsManager;
    }
    
    public IslandAnimationManager getIslandAnimationManager() {
        return islandAnimationManager;
    }
    
    public PetManager getPetManager() {
        return petManager;
    }
    
    // Gettery Structure.sk
    public StructureManager getStructureManager() {
        return structureManager;
    }
    
    public ObjectiveManager getObjectiveManager() {
        return objectiveManager;
    }
    
    public BankManager getBankManager() {
        return bankManager;
    }
    
    public WarpManager getWarpManager() {
        return warpManager;
    }
    
    // Gettery Skyblock.sk i Minions.sk
    public MinionManager getMinionManager() {
        return minionManager;
    }
    
    public SpawnCommand getSpawnCommand() {
        return spawnCommand;
    }
    
    public CaveManager getCaveManager() {
        return caveManager;
    }
    
    // Gettery Dungeons.sk
    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }
    
    // Gettery Cinematic.sk i BossbarStats.sk
    public CinematicManager getCinematicManager() {
        return cinematicManager;
    }
    
    public BossbarStatsManager getBossbarStatsManager() {
        return bossbarStatsManager;
    }
    
    public me.clip.placeholderapi.PlaceholderAPI getPlaceholderAPI() {
        return placeholderAPI;
    }
    
    public net.milkbowl.vault.economy.Economy getEconomy() {
        return economy;
    }
}
