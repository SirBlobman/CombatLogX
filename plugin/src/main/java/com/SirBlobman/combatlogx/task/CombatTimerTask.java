package com.SirBlobman.combatlogx.task;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import com.SirBlobman.api.utility.Validate;
import com.SirBlobman.combatlogx.CombatPlugin;
import com.SirBlobman.combatlogx.api.event.PlayerCombatTimerChangeEvent;
import com.SirBlobman.combatlogx.manager.CombatManager;

public final class CombatTimerTask extends BukkitRunnable {
    private final CombatPlugin plugin;
    public CombatTimerTask(CombatPlugin plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
    }

    public void start() {
        runTaskTimerAsynchronously(this.plugin, 5L, 1L);
    }

    @Override
    public void run() {
        CombatManager combatManager = this.plugin.getCombatManager();
        List<Player> playerList = combatManager.getPlayersInCombat();
        playerList.forEach(this::triggerEvent);
    }

    private void triggerEvent(Player player) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        PlayerCombatTimerChangeEvent event = new PlayerCombatTimerChangeEvent(player);
        pluginManager.callEvent(event);
    }
}