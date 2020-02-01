package com.SirBlobman.combatlogx.expansion.notifier;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.expansion.notifier.listener.ListenerNotifier;
import com.SirBlobman.combatlogx.expansion.notifier.utility.ActionBarManager;
import com.SirBlobman.combatlogx.expansion.notifier.utility.BossBarManager;
import com.SirBlobman.combatlogx.expansion.notifier.utility.scoreboard.ScoreboardHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class Notifier extends Expansion {
    public Notifier(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public String getUnlocalizedName() {
        return "Notifier";
    }

    @Override
    public String getVersion() {
        return "15.0";
    }

    @Override
    public void onLoad() {
        saveDefaultConfig("actionbar.yml");
        saveDefaultConfig("bossbar.yml");
        saveDefaultConfig("scoreboard.yml");
        saveDefaultConfig("mvdw.yml");
        saveDefaultConfig("title-manager.yml");
    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new ListenerNotifier(this), getPlugin().getPlugin());

        Logger logger = getLogger();
        if(manager.isPluginEnabled("MVdWPlaceholderAPI")) {
            Plugin plugin = manager.getPlugin("MVdWPlaceholderAPI");
            if(plugin != null) {
                String version = plugin.getDescription().getVersion();
                logger.info("Successfully hooked into MVdWPlaceholderAPI v" + version);
            }
        }

        if(manager.isPluginEnabled("TitleManager")) {
            Plugin plugin = manager.getPlugin("TitleManager");
            if(plugin != null) {
                String version = plugin.getDescription().getVersion();
                logger.info("Successfully hooked into TitleManager v" + version);
            }
        }
    }

    @Override
    public void onDisable() {
        List<Player> onlinePlayerList = new ArrayList<>(Bukkit.getOnlinePlayers());
        for(Player player : onlinePlayerList) {
            if(!ScoreboardHandler.isDisabled(player)) ScoreboardHandler.disableScoreboard(this, player);
            if(!ActionBarManager.isDisabled(player)) ActionBarManager.removeActionBar(this, player);
            if(!BossBarManager.isDisabled(player)) BossBarManager.removeBossBar(this, player, true);
        }
    }

    @Override
    public void reloadConfig() {
        reloadConfig("actionbar.yml");
        reloadConfig("bossbar.yml");
        reloadConfig("scoreboard.yml");
        reloadConfig("mvdw.yml");
        reloadConfig("title-manager.yml");
    }
}