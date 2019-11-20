package com.SirBlobman.combatlogx.expansion.notifier;

import java.util.List;
import java.util.stream.Collectors;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.expansion.notifier.listener.ListenerNotifier;
import com.SirBlobman.combatlogx.expansion.notifier.utility.ActionBarHandler;
import com.SirBlobman.combatlogx.expansion.notifier.utility.BossBarHandler;
import com.SirBlobman.combatlogx.expansion.notifier.utility.scoreboard.ScoreboardHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new ListenerNotifier(this), getPlugin().getPlugin());
    }

    @Override
    public void onDisable() {
        List<Player> onlinePlayerList = Bukkit.getOnlinePlayers().stream().collect(Collectors.toList());
        for(Player player : onlinePlayerList) {
            if(!ScoreboardHandler.isDisabled(player)) ScoreboardHandler.disableScoreboard(this, player);
            if(!ActionBarHandler.isDisabled(player)) ActionBarHandler.removeActionBar(this, player);
            if(!BossBarHandler.isDisabled(player)) BossBarHandler.removeBossBar(this, player, true);
        }
    }

    @Override
    public void reloadConfig() {
        reloadConfig("actionbar.yml");
        reloadConfig("bossbar.yml");
        reloadConfig("scoreboard.yml");
    }
}