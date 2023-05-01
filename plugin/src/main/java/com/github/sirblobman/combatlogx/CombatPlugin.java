package com.github.sirblobman.combatlogx;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.language.Language;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.api.update.SpigotUpdateManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.CommandConfiguration;
import com.github.sirblobman.combatlogx.api.configuration.MainConfiguration;
import com.github.sirblobman.combatlogx.api.configuration.PunishConfiguration;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.ICrystalManager;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;
import com.github.sirblobman.combatlogx.api.manager.IForgiveManager;
import com.github.sirblobman.combatlogx.api.manager.IPlaceholderManager;
import com.github.sirblobman.combatlogx.api.manager.IPunishManager;
import com.github.sirblobman.combatlogx.api.manager.ITimerManager;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import com.github.sirblobman.combatlogx.command.CommandCombatTimer;
import com.github.sirblobman.combatlogx.command.CommandTogglePVP;
import com.github.sirblobman.combatlogx.command.combatlogx.CommandCombatLogX;
import com.github.sirblobman.combatlogx.configuration.ConfigurationChecker;
import com.github.sirblobman.combatlogx.listener.ListenerConfiguration;
import com.github.sirblobman.combatlogx.listener.ListenerDamage;
import com.github.sirblobman.combatlogx.listener.ListenerDeath;
import com.github.sirblobman.combatlogx.listener.ListenerEndCrystal;
import com.github.sirblobman.combatlogx.listener.ListenerInvulnerable;
import com.github.sirblobman.combatlogx.listener.ListenerPunish;
import com.github.sirblobman.combatlogx.listener.ListenerUntag;
import com.github.sirblobman.combatlogx.manager.CombatManager;
import com.github.sirblobman.combatlogx.manager.CrystalManager;
import com.github.sirblobman.combatlogx.manager.DeathManager;
import com.github.sirblobman.combatlogx.manager.ForgiveManager;
import com.github.sirblobman.combatlogx.manager.PlaceholderManager;
import com.github.sirblobman.combatlogx.manager.PunishManager;
import com.github.sirblobman.combatlogx.placeholder.BasePlaceholderExpansion;
import com.github.sirblobman.combatlogx.task.TimerUpdateTask;
import com.github.sirblobman.combatlogx.task.UntagTask;
import com.github.sirblobman.api.shaded.bstats.bukkit.Metrics;
import com.github.sirblobman.api.shaded.bstats.charts.SimplePie;

public final class CombatPlugin extends ConfigurablePlugin implements ICombatLogX {
    private final TimerUpdateTask timerUpdateTask;
    private final CombatManager combatManager;
    private final PunishManager punishManager;
    private final ExpansionManager expansionManager;
    private final PlaceholderManager placeholderManager;
    private final DeathManager deathManager;
    private final ForgiveManager forgiveManager;
    private final CrystalManager crystalManager;

    private final MainConfiguration configuration;
    private final CommandConfiguration commandConfiguration;
    private final PunishConfiguration punishConfiguration;

    public CombatPlugin() {
        this.timerUpdateTask = new TimerUpdateTask(this);
        this.expansionManager = new ExpansionManager(this);
        this.placeholderManager = new PlaceholderManager(this);
        this.combatManager = new CombatManager(this);
        this.punishManager = new PunishManager(this);
        this.deathManager = new DeathManager(this);
        this.forgiveManager = new ForgiveManager(this);
        this.crystalManager = new CrystalManager(this);

        this.configuration = new MainConfiguration(this);
        this.commandConfiguration = new CommandConfiguration();
        this.punishConfiguration = new PunishConfiguration();
    }

    @Override
    public void onLoad() {
        ConfigurationChecker configurationChecker = new ConfigurationChecker(this);
        configurationChecker.checkVersion();

        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
        configurationManager.saveDefault("commands.yml");
        configurationManager.saveDefault("punish.yml");

        LanguageManager languageManager = getLanguageManager();
        languageManager.saveDefaultLanguageFiles();

        ExpansionManager expansionManager = getExpansionManager();
        expansionManager.loadExpansions();
    }

    @Override
    public void onEnable() {
        onReload();

        LanguageManager languageManager = getLanguageManager();
        languageManager.onPluginEnable();

        broadcastMessageOnLoad();

        registerCommands();
        registerListeners();
        registerTasks();
        registerExpansions();
        registerUpdates();
        registerBasePlaceholders();

        broadcastMessageOnEnable();
        register_bStats();
    }

    @Override
    public void onDisable() {
        untagAllPlayers();

        ExpansionManager expansionManager = getExpansionManager();
        expansionManager.disableExpansions();

        broadcastMessageOnDisable();
    }

    @Override
    public @NotNull ConfigurablePlugin getPlugin() {
        return this;
    }

    @Override
    public void onReload() {
        ConfigurationManager configurationManager = getConfigurationManager();
        List<String> fileNameList = Arrays.asList("commands.yml", "config.yml", "punish.yml");
        for (String fileName : fileNameList) {
            configurationManager.reload(fileName);
        }

        reloadLanguage();

        getConfiguration().load(configurationManager.get("config.yml"));
        getCommandConfiguration().load(configurationManager.get("commands.yml"));
        getPunishConfiguration().load(configurationManager.get("punish.yml"));

        ExpansionManager expansionManager = getExpansionManager();
        expansionManager.reloadConfigs();
    }

    @Override
    public @NotNull ExpansionManager getExpansionManager() {
        return this.expansionManager;
    }

    @Override
    public @NotNull ICombatManager getCombatManager() {
        return this.combatManager;
    }

    @Override
    public @NotNull IPunishManager getPunishManager() {
        return this.punishManager;
    }

    @Override
    public @NotNull ITimerManager getTimerManager() {
        return this.timerUpdateTask;
    }

    @Override
    public @NotNull IDeathManager getDeathManager() {
        return this.deathManager;
    }

    @Override
    public @NotNull IPlaceholderManager getPlaceholderManager() {
        return this.placeholderManager;
    }

    @Override
    public @NotNull IForgiveManager getForgiveManager() {
        return this.forgiveManager;
    }

    @Override
    public boolean isDebugModeDisabled() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return !configuration.getBoolean("debug-mode", false);
    }

    @Override
    public void printDebug(String @NotNull ... messageArray) {
        if (isDebugModeDisabled()) {
            return;
        }

        Logger logger = getLogger();
        for (String message : messageArray) {
            String realMessage = ("[Debug] " + message);
            logger.info(realMessage);
        }
    }

    @Override
    public void printDebug(@NotNull Throwable ex) {
        if (isDebugModeDisabled()) {
            return;
        }

        Logger logger = getLogger();
        logger.log(Level.WARNING, "[Debug] Full Error Details:", ex);
    }

    @Override
    public @NotNull MainConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public @NotNull CommandConfiguration getCommandConfiguration() {
        return this.commandConfiguration;
    }

    @Override
    public @NotNull PunishConfiguration getPunishConfiguration() {
        return this.punishConfiguration;
    }

    @Override
    public @NotNull ICrystalManager getCrystalManager() {
        return this.crystalManager;
    }

    @Override
    public @NotNull String getKeyName() {
        return "combatlogx";
    }

    private void reloadLanguage() {
        LanguageManager languageManager = getLanguageManager();
        languageManager.reloadLanguages();
    }

    private void registerCommands() {
        new CommandCombatLogX(this).register();
        new CommandCombatTimer(this).register();
        new CommandTogglePVP(this).register();
    }

    private void registerListeners() {
        new ListenerConfiguration(this).register();
        new ListenerDamage(this).register();
        new ListenerPunish(this).register();
        new ListenerUntag(this).register();
        new ListenerDeath(this).register();
        new ListenerInvulnerable(this).register();

        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion > 13) {
            new ListenerEndCrystal(this).register();
        }
    }

    private void registerTasks() {
        ITimerManager timerManager = getTimerManager();
        timerManager.register();

        new UntagTask(this).register();
    }

    private void registerExpansions() {
        ExpansionManager expansionManager = getExpansionManager();
        expansionManager.enableExpansions();
    }

    private void registerUpdates() {
        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        SpigotUpdateManager updateManager = corePlugin.getSpigotUpdateManager();
        updateManager.addResource(this, 31689L);
    }

    private void untagAllPlayers() {
        ICombatManager combatManager = getCombatManager();
        List<Player> playerCombatList = combatManager.getPlayersInCombat();
        for (Player player : playerCombatList) {
            combatManager.untag(player, UntagReason.EXPIRE);
        }
    }

    private void broadcastMessageOnLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if (!configuration.getBoolean("broadcast.on-load")) {
            return;
        }

        LanguageManager languageManager = getLanguageManager();
        languageManager.broadcastMessage("broadcast.on-load", null);
    }

    private void broadcastMessageOnEnable() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if (!configuration.getBoolean("broadcast.on-enable")) {
            return;
        }

        LanguageManager languageManager = getLanguageManager();
        languageManager.broadcastMessage("broadcast.on-enable", null);
    }

    private void broadcastMessageOnDisable() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if (!configuration.getBoolean("broadcast.on-disable")) {
            return;
        }

        LanguageManager languageManager = getLanguageManager();
        languageManager.broadcastMessage("broadcast.on-disable", null);
    }

    private void registerBasePlaceholders() {
        BasePlaceholderExpansion placeholderExpansion = new BasePlaceholderExpansion(this);
        IPlaceholderManager placeholderManager = getPlaceholderManager();
        placeholderManager.registerPlaceholderExpansion(placeholderExpansion);
    }

    private void register_bStats() {
        Metrics metrics = new Metrics(this, 16090);
        metrics.addCustomChart(new SimplePie("selected_language", this::getDefaultLanguageCode));
    }

    private String getDefaultLanguageCode() {
        LanguageManager languageManager = getLanguageManager();
        Language defaultLanguage = languageManager.getDefaultLanguage();
        return (defaultLanguage == null ? "none" : defaultLanguage.getLanguageName());
    }
}
