package com.github.sirblobman.combatlogx.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.MainConfiguration;
import com.github.sirblobman.combatlogx.api.listener.CombatListener;
import com.github.sirblobman.combatlogx.task.PlayerVulnerableTask;

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
        PlayerVulnerableTask task = new PlayerVulnerableTask(getCombatLogX(), player);
        task.setDelay(2L);

        TaskScheduler scheduler = getCombatLogX().getFoliaHelper().getScheduler();
        scheduler.scheduleEntityTask(task);
    }
}
