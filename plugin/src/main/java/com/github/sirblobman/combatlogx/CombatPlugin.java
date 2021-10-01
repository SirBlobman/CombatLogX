package com.github.sirblobman.combatlogx;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.api.update.UpdateManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.combatlogx.api.manager.IPunishManager;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import com.github.sirblobman.combatlogx.command.combatlogx.CommandCombatLogX;
import com.github.sirblobman.combatlogx.command.CommandCombatTimer;
import com.github.sirblobman.combatlogx.command.CommandTogglePVP;
import com.github.sirblobman.combatlogx.configuration.ConfigurationChecker;
import com.github.sirblobman.combatlogx.listener.ListenerConfiguration;
import com.github.sirblobman.combatlogx.listener.ListenerDamage;
import com.github.sirblobman.combatlogx.listener.ListenerDeath;
import com.github.sirblobman.combatlogx.listener.ListenerPunish;
import com.github.sirblobman.combatlogx.listener.ListenerUntag;
import com.github.sirblobman.combatlogx.manager.CombatManager;
import com.github.sirblobman.combatlogx.manager.PunishManager;
import com.github.sirblobman.combatlogx.task.TimerUpdateTask;
import com.github.sirblobman.combatlogx.task.UntagTask;

public final class CombatPlugin extends ConfigurablePlugin implements ICombatLogX {
    private final CombatManager combatManager;
    private final PunishManager punishManager;
    private final ExpansionManager expansionManager;
    private final ListenerDeath listenerDeath;
    private final TimerUpdateTask timerUpdateTask;

    public CombatPlugin() {
        this.expansionManager = new ExpansionManager(this);
        this.combatManager = new CombatManager(this);
        this.punishManager = new PunishManager(this);
        this.listenerDeath = new ListenerDeath(this);
        this.timerUpdateTask = new TimerUpdateTask(this);
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
        languageManager.saveDefaultLanguages();
        languageManager.reloadLanguages();

        ExpansionManager expansionManager = getExpansionManager();
        expansionManager.loadExpansions();

        broadcastLoadMessage();
    }

    @Override
    public void onEnable() {
        new CommandCombatLogX(this).register();
        new CommandCombatTimer(this).register();
        new CommandTogglePVP(this).register();

        new ListenerConfiguration(this).register();
        new ListenerDamage(this).register();
        new ListenerPunish(this).register();
        new ListenerUntag(this).register();
        getDeathListener().register();

        this.timerUpdateTask.register();
        new UntagTask(this).register();

        ExpansionManager expansionManager = getExpansionManager();
        expansionManager.enableExpansions();
        broadcastEnableMessage();

        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        UpdateManager updateManager = corePlugin.getUpdateManager();
        updateManager.addResource(this, 31689L);
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
    public ExpansionManager getExpansionManager() {
        return this.expansionManager;
    }

    @Override
    public CombatManager getCombatManager() {
        return this.combatManager;
    }

    @Override
    public IPunishManager getPunishManager() {
        return this.punishManager;
    }

    @Override
    public TimerUpdateTask getTimerManager() {
        return this.timerUpdateTask;
    }

    @Override
    public ListenerDeath getDeathListener() {
        return this.listenerDeath;
    }

    @Override
    public void sendMessageWithPrefix(CommandSender sender, String key, Replacer replacer, boolean color) {
        String message = getMessageWithPrefix(sender, key, replacer, color);
        if(!message.isEmpty()) sender.sendMessage(message);
    }

    @Override
    public String getMessageWithPrefix(CommandSender sender, String key, Replacer replacer, boolean color) {
        LanguageManager languageManager = getLanguageManager();
        String message = languageManager.getMessage(sender, key, replacer, color);
        if(message.isEmpty()) return "";

        String prefix = languageManager.getMessage(sender, "prefix", null, true);
        if(prefix.isEmpty()) return message;

        if(!color) ChatColor.stripColor(prefix);
        return String.format(Locale.US,"%s %s", prefix, message);
    }

    @Override
    public void sendMessage(CommandSender sender, String... messageArray) {
        for(String message : messageArray) {
            if(message == null || message.isEmpty()) continue;
            sender.sendMessage(message);
        }
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

    @Override
    public void printDebug(Throwable ex) {
        YamlConfiguration configuration = getConfig("config.yml");
        if(!configuration.getBoolean("debug-mode")) return;

        Logger logger = getLogger();
        logger.log(Level.WARNING, "Full Error Details:", ex);
    }

    private void untagAllPlayers() {
        ICombatManager combatManager = getCombatManager();
        List<Player> playerCombatList = combatManager.getPlayersInCombat();
        for(Player player : playerCombatList) {
            combatManager.untag(player, UntagReason.EXPIRE);
        }
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
