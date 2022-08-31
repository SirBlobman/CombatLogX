package com.github.sirblobman.combatlogx;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.adventure.adventure.audience.Audience;
import com.github.sirblobman.api.adventure.adventure.platform.bukkit.BukkitAudiences;
import com.github.sirblobman.api.adventure.adventure.text.Component;
import com.github.sirblobman.api.adventure.adventure.text.TextComponent.Builder;
import com.github.sirblobman.api.bstats.bukkit.Metrics;
import com.github.sirblobman.api.bstats.charts.SimplePie;
import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.language.Language;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.api.update.UpdateManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;
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
import com.github.sirblobman.combatlogx.listener.ListenerInvulnerable;
import com.github.sirblobman.combatlogx.listener.ListenerPunish;
import com.github.sirblobman.combatlogx.listener.ListenerUntag;
import com.github.sirblobman.combatlogx.manager.CombatManager;
import com.github.sirblobman.combatlogx.manager.DeathManager;
import com.github.sirblobman.combatlogx.manager.PlaceholderManager;
import com.github.sirblobman.combatlogx.manager.PunishManager;
import com.github.sirblobman.combatlogx.placeholder.BasePlaceholderExpansion;
import com.github.sirblobman.combatlogx.task.TimerUpdateTask;
import com.github.sirblobman.combatlogx.task.UntagTask;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CombatPlugin extends ConfigurablePlugin implements ICombatLogX {
    private final TimerUpdateTask timerUpdateTask;
    private final CombatManager combatManager;
    private final PunishManager punishManager;
    private final ExpansionManager expansionManager;
    private final PlaceholderManager placeholderManager;
    private final DeathManager deathManager;

    public CombatPlugin() {
        this.timerUpdateTask = new TimerUpdateTask(this);
        this.expansionManager = new ExpansionManager(this);
        this.placeholderManager = new PlaceholderManager(this);
        this.combatManager = new CombatManager(this);
        this.punishManager = new PunishManager(this);
        this.deathManager = new DeathManager(this);
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
        broadcastLoadMessage();

        registerCommands();
        registerListeners();
        registerTasks();
        registerExpansions();
        registerUpdates();
        registerBasePlaceholders();

        broadcastEnableMessage();
        registerbStats();
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
    public JavaPlugin getPlugin() {
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

        IPunishManager punishManager = getPunishManager();
        punishManager.loadPunishments();

        ExpansionManager expansionManager = getExpansionManager();
        expansionManager.reloadConfigs();

        ICombatManager combatManager = getCombatManager();
        combatManager.onReload();
    }

    @Override
    public ExpansionManager getExpansionManager() {
        return this.expansionManager;
    }

    @Override
    public ICombatManager getCombatManager() {
        return this.combatManager;
    }

    @Override
    public IPunishManager getPunishManager() {
        return this.punishManager;
    }

    @Override
    public ITimerManager getTimerManager() {
        return this.timerUpdateTask;
    }

    @Override
    public IDeathManager getDeathManager() {
        return this.deathManager;
    }

    @Override
    public IPlaceholderManager getPlaceholderManager() {
        return this.placeholderManager;
    }

    @NotNull
    @Override
    public Component getMessageWithPrefix(@Nullable CommandSender audience, @NotNull String key,
                                          @Nullable Replacer replacer) {
        LanguageManager languageManager = getLanguageManager();
        Component message = languageManager.getMessage(audience, key, replacer);
        if (Component.empty().equals(message)) {
            return Component.empty();
        }

        Component prefix = languageManager.getMessage(audience, "prefix", null);
        if (Component.empty().equals(prefix)) {
            return message;
        }

        Builder builder = Component.text();
        builder.append(prefix);
        builder.append(Component.space());
        builder.append(message);
        return builder.build();
    }

    @Override
    public void sendMessageWithPrefix(@NotNull CommandSender audience, @NotNull String key,
                                      @Nullable Replacer replacer) {
        Component message = getMessageWithPrefix(audience, key, replacer);
        if (Component.empty().equals(message)) {
            return;
        }

        LanguageManager languageManager = getLanguageManager();
        BukkitAudiences audiences = languageManager.getAudiences();
        if (audiences == null) {
            return;
        }

        Audience realAudience = audiences.sender(audience);
        realAudience.sendMessage(message);
    }

    @Override
    public void sendMessage(CommandSender sender, String... messageArray) {
        sender.sendMessage(messageArray);
    }

    @Override
    public boolean isDebugModeDisabled() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return !configuration.getBoolean("debug-mode", false);
    }

    @Override
    public void printDebug(String... messageArray) {
        if(isDebugModeDisabled()) {
            return;
        }

        Logger logger = getLogger();
        for (String message : messageArray) {
            String realMessage = ("[Debug] " + message);
            logger.info(realMessage);
        }
    }

    @Override
    public void printDebug(Throwable ex) {
        if(isDebugModeDisabled()) {
            return;
        }

        Logger logger = getLogger();
        logger.log(Level.WARNING, "[Debug] Full Error Details:", ex);
    }

    @Override
    public String getKeyName() {
        return "combatlogx";
    }

    private void reloadLanguage() {
        LanguageManager languageManager = getLanguageManager();
        languageManager.reloadLanguageFiles();
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
        UpdateManager updateManager = corePlugin.getUpdateManager();
        updateManager.addResource(this, 31689L);
    }

    private void untagAllPlayers() {
        ICombatManager combatManager = getCombatManager();
        List<Player> playerCombatList = combatManager.getPlayersInCombat();
        for (Player player : playerCombatList) {
            combatManager.untag(player, UntagReason.EXPIRE);
        }
    }

    private void broadcastLoadMessage() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if (!configuration.getBoolean("broadcast.on-load")) {
            return;
        }

        LanguageManager languageManager = getLanguageManager();
        languageManager.broadcastMessage("broadcast.on-load", null, null);
    }

    private void broadcastEnableMessage() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if (!configuration.getBoolean("broadcast.on-enable")) {
            return;
        }

        LanguageManager languageManager = getLanguageManager();
        languageManager.broadcastMessage("broadcast.on-enable", null, null);
    }

    private void broadcastDisableMessage() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if (!configuration.getBoolean("broadcast.on-disable")) {
            return;
        }

        LanguageManager languageManager = getLanguageManager();
        languageManager.broadcastMessage("broadcast.on-disable", null, null);
    }

    private void registerBasePlaceholders() {
        BasePlaceholderExpansion placeholderExpansion = new BasePlaceholderExpansion(this);
        IPlaceholderManager placeholderManager = getPlaceholderManager();
        placeholderManager.registerPlaceholderExpansion(placeholderExpansion);
    }

    private void registerbStats() {
        Metrics metrics = new Metrics(this, 16090);
        metrics.addCustomChart(new SimplePie("selected_language", this::getDefaultLanguageCode));
    }

    private String getDefaultLanguageCode() {
        LanguageManager languageManager = getLanguageManager();
        Language defaultLanguage = languageManager.getDefaultLanguage();
        return (defaultLanguage == null ? "none" : defaultLanguage.getLanguageCode());
    }
}
