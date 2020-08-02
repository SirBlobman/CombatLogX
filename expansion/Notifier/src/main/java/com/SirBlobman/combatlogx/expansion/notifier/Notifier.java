package com.SirBlobman.combatlogx.expansion.notifier;

import java.util.Collection;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.expansion.notifier.command.CommandNotifier;
import com.SirBlobman.combatlogx.expansion.notifier.hook.HookMVdWPlaceholderAPI;
import com.SirBlobman.combatlogx.expansion.notifier.hook.HookPlaceholderAPI;
import com.SirBlobman.combatlogx.expansion.notifier.listener.ListenerNotifier;
import com.SirBlobman.combatlogx.expansion.notifier.manager.ActionBarManager;
import com.SirBlobman.combatlogx.expansion.notifier.manager.BossBarManager;
import com.SirBlobman.combatlogx.expansion.notifier.manager.ScoreBoardManager;
import com.SirBlobman.combatlogx.utility.PlaceholderReplacer;

public class Notifier extends Expansion {
    private final ActionBarManager actionBarManager;
    private final BossBarManager bossBarManager;
    private final ScoreBoardManager scoreBoardManager;
    public Notifier(ICombatLogX plugin) {
        super(plugin);
        this.actionBarManager = new ActionBarManager(this);
        this.bossBarManager = new BossBarManager(this);
        this.scoreBoardManager = new ScoreBoardManager(this);
    }

    @Override
    public void onLoad() {
        saveDefaultConfig("actionbar.yml");
        saveDefaultConfig("bossbar.yml");
        saveDefaultConfig("scoreboard.yml");
        saveDefaultConfig("mvdw.yml");
    }

    @Override
    public void onEnable() {
        ICombatLogX combat = getPlugin();
        JavaPlugin plugin = combat.getPlugin();
        
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new ListenerNotifier(this), plugin);
    
        CommandNotifier commandNotifier = new CommandNotifier(this);
        combat.registerCommand("notifier", commandNotifier, "Toggle the boss bar, scoreboard, and action bar.", "/notifier bossbar/actionbar/scoreboard", "clx-toggle");

        hookIfEnabled("MVdWPlaceholderAPI");
        hookIfEnabled("PlaceholderAPI");
        hookIfEnabled("TitleManager");
    }

    @Override
    public void onDisable() {
        BossBarManager bossBarManager = getBossBarManager();
        ActionBarManager actionBarManager = getActionBarManager();
        ScoreBoardManager scoreBoardManager = getScoreBoardManager();
    
        Collection<? extends Player> onlinePlayerList = Bukkit.getOnlinePlayers();
        for(Player player : onlinePlayerList) {
            actionBarManager.removeActionBar(player);
            bossBarManager.removeBossBar(player, true);
            scoreBoardManager.removeScoreboard(player);
        }
    }

    @Override
    public void reloadConfig() {
        reloadConfig("actionbar.yml");
        reloadConfig("bossbar.yml");
        reloadConfig("scoreboard.yml");
        reloadConfig("mvdw.yml");
    }
    
    public ActionBarManager getActionBarManager() {
        return this.actionBarManager;
    }
    
    public BossBarManager getBossBarManager() {
        return this.bossBarManager;
    }
    
    public ScoreBoardManager getScoreBoardManager() {
        return this.scoreBoardManager;
    }
    
    public String replacePlaceholders(Player player, String string) {
        if(player == null) return string;
        ICombatLogX plugin = getPlugin();
        
        PluginManager manager = Bukkit.getPluginManager();
        if(manager.isPluginEnabled("PlaceholderAPI")) string = HookPlaceholderAPI.replacePlaceholders(player, string);
        if(manager.isPluginEnabled("MVdWPlaceholderAPI")) string = HookMVdWPlaceholderAPI.replacePlaceholders(player, string);
        
        String timeLeft = PlaceholderReplacer.getTimeLeftSeconds(plugin, player);
        String inCombat = PlaceholderReplacer.getInCombat(plugin, player);
        String combatStatus = PlaceholderReplacer.getCombatStatus(plugin, player);
    
        String enemyName = PlaceholderReplacer.getEnemyName(plugin, player);
        String enemyHealth = PlaceholderReplacer.getEnemyHealth(plugin, player);
        String enemyHealthRounded = PlaceholderReplacer.getEnemyHealthRounded(plugin, player);
        String enemyHearts = PlaceholderReplacer.getEnemyHearts(plugin, player);
        
        return string.replace("{time_left}", timeLeft).replace("{in_combat}", inCombat)
                .replace("{status}", combatStatus).replace("{enemy_name}", enemyName)
                .replace("{enemy_health}", enemyHealth)
                .replace("{enemy_health_rounded}", enemyHealthRounded)
                .replace("{enemy_hearts}", enemyHearts);
    }
    
    private void hookIfEnabled(String pluginName) {
        PluginManager manager = Bukkit.getPluginManager();
        if(!manager.isPluginEnabled(pluginName)) return;
        
        Plugin plugin = manager.getPlugin(pluginName);
        if(plugin == null) return;
    
        PluginDescriptionFile description = plugin.getDescription();
        String nameAndVersion = description.getFullName();
    
        Logger logger = getLogger();
        logger.info("Successfully hooked into " + nameAndVersion);
    }
}