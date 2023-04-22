package com.github.sirblobman.combatlogx.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.MainConfiguration;
import com.github.sirblobman.combatlogx.api.listener.CombatListener;

import org.jetbrains.annotations.NotNull;

public final class ListenerInvulnerable extends CombatListener {
    public ListenerInvulnerable(@NotNull ICombatLogX plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        checkEvent(e);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerTeleportEvent e) {
        checkEvent(e);
    }

    private void checkEvent(PlayerEvent e) {
        if (isDisabled()) {
            return;
        }

        Player player = e.getPlayer();
        setVulnerableLater(player);
    }

    private boolean isDisabled() {
        ICombatLogX plugin = getCombatLogX();
        MainConfiguration configuration = plugin.getConfiguration();
        return !configuration.isRemoveNoDamageCooldown();
    }

    private void setVulnerableLater(Player player) {
        JavaPlugin plugin = getJavaPlugin();
        Runnable task = () -> setVulnerable(player);
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(plugin, task, 2L);
    }

    private void setVulnerable(Player player) {
        player.setNoDamageTicks(0);
    }
}
