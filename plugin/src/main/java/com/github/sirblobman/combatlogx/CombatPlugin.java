package com.github.sirblobman.combatlogx;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.update.UpdateChecker;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import com.github.sirblobman.combatlogx.command.CommandCombatLogX;
import com.github.sirblobman.combatlogx.command.CommandCombatTimer;
import com.github.sirblobman.combatlogx.command.CommandTogglePVP;
import com.github.sirblobman.combatlogx.configuration.ConfigurationChecker;
import com.github.sirblobman.combatlogx.listener.ListenerConfiguration;
import com.github.sirblobman.combatlogx.listener.ListenerDamage;
import com.github.sirblobman.combatlogx.listener.ListenerDeath;
import com.github.sirblobman.combatlogx.listener.ListenerPunish;
import com.github.sirblobman.combatlogx.listener.ListenerUntag;
import com.github.sirblobman.combatlogx.manager.CombatManager;
import com.github.sirblobman.combatlogx.task.CombatTimerTask;

public final class CombatPlugin extends JavaPlugin implements ICombatLogX {
    private final ConfigurationManager configurationManager;
    private final PlayerDataManager playerDataManager;
    private final LanguageManager languageManager;
    private final CombatManager combatManager;
    private final ExpansionManager expansionManager;
    private final ListenerDeath listenerDeath;
    private final UpdateChecker updateChecker;
    public CombatPlugin() {
        this.configurationManager = new ConfigurationManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.languageManager = new LanguageManager(this, this.configurationManager);
        this.expansionManager = new ExpansionManager(this);
        this.combatManager = new CombatManager(this);
        this.listenerDeath = new ListenerDeath(this);
        this.updateChecker = new UpdateChecker(this, 31689L);
    }

    @Override
    public void onLoad() {
        ConfigurationChecker configurationChecker = new ConfigurationChecker(this);
        configurationChecker.checkVersion();

        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
        configurationManager.saveDefault("commands.yml");
        configurationManager.saveDefault("force-field.yml");
        configurationManager.saveDefault("punish.yml");

        configurationManager.saveDefault("language.yml");
        configurationManager.saveDefault("language/en_us.lang.yml");

        ExpansionManager expansionManager = getExpansionManager();
        expansionManager.loadExpansions();
        broadcastLoadMessage();
    }

    @Override
    public void onEnable() {
        ListenerDeath deathListener = getDeathListener();
        deathListener.register();

        new ListenerConfiguration(this).register();
        new ListenerDamage(this).register();
        new ListenerPunish(this).register();
        new ListenerUntag(this).register();

        new CommandCombatLogX(this).register();
        new CommandCombatTimer(this).register();
        new CommandTogglePVP(this).register();

        CombatTimerTask combatTimerTask = new CombatTimerTask(this);
        combatTimerTask.start();

        ExpansionManager expansionManager = getExpansionManager();
        expansionManager.enableExpansions();
        broadcastEnableMessage();

        UpdateChecker updateChecker = getUpdateChecker();
        updateChecker.runCheck();
    }

    @Override
    public void onDisable() {
        untagAllPlayers();
        ExpansionManager expansionManager = getExpansionManager();
        expansionManager.disableExpansions();

        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
        broadcastDisableMessage();
    }

    @Override
    public CombatPlugin getPlugin() {
        return this;
    }

    @Override
    public ClassLoader getPluginClassLoader() {
        return getClassLoader();
    }

    @Override
    public YamlConfiguration getConfig(String fileName) {
        ConfigurationManager configurationManager = getConfigurationManager();
        return configurationManager.get(fileName);
    }

    @Override
    public void reloadConfig(String fileName) {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload(fileName);
    }

    @Override
    public void saveConfig(String fileName) {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.save(fileName);
    }

    @Override
    public void saveDefaultConfig(String fileName) {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault(fileName);
    }

    @Override
    public YamlConfiguration getData(OfflinePlayer player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        return playerDataManager.get(player);
    }

    @Override
    public void saveData(OfflinePlayer player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        playerDataManager.save(player);
    }

    @Override
    public MultiVersionHandler getMultiVersionHandler() {
        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        return corePlugin.getMultiVersionHandler();
    }

    @Override
    public ConfigurationManager getConfigurationManager() {
        return this.configurationManager;
    }

    @Override
    public PlayerDataManager getPlayerDataManager() {
        return this.playerDataManager;
    }

    @Override
    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }

    @Override
    public ExpansionManager getExpansionManager() {
        return this.expansionManager;
    }

    @Override
    public CombatManager getCombatManager() {
        return this.combatManager;
    }

    @Override
    public void printDebug(String... messageArray) {
        YamlConfiguration configuration = getConfig("config.yml");
        if(!configuration.getBoolean("debug-mode")) return;

        Logger logger = getLogger();
        for(String message : messageArray) {
            String realMessage = ("[Debug] " + message);
            logger.info(realMessage);
        }
    }

    public ListenerDeath getDeathListener() {
        return this.listenerDeath;
    }

    public UpdateChecker getUpdateChecker() {
        return this.updateChecker;
    }

    private void untagAllPlayers() {
        CombatManager combatManager = getCombatManager();
        List<Player> playerList = combatManager.getPlayersInCombat();
        playerList.forEach(player -> combatManager.untag(player, UntagReason.EXPIRE));
    }

    private void broadcastLoadMessage() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if(!configuration.getBoolean("broadcast.on-load")) return;

        LanguageManager languageManager = getLanguageManager();
        languageManager.broadcastMessage("broadcast.on-load", null, true);
    }

    private void broadcastEnableMessage() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if(!configuration.getBoolean("broadcast.on-enable")) return;

        LanguageManager languageManager = getLanguageManager();
        languageManager.broadcastMessage("broadcast.on-enable", null, true);
    }

    private void broadcastDisableMessage() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if(!configuration.getBoolean("broadcast.on-disable")) return;

        LanguageManager languageManager = getLanguageManager();
        languageManager.broadcastMessage("broadcast.on-disable", null, true);
    }
}