package com.github.sirblobman.combatlogx.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.PunishConfiguration;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.listener.CombatListener;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

import org.jetbrains.annotations.NotNull;

public final class ListenerPunish extends CombatListener {
    public ListenerPunish(@NotNull ICombatLogX plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforePunish(PlayerPunishEvent e) {
        UntagReason untagReason = e.getPunishReason();
        if (shouldPunishForReason(untagReason)) {
            return;
        }

        e.setCancelled(true);
    }

    private boolean shouldPunishForReason(UntagReason reason) {
        ICombatLogX plugin = getCombatLogX();
        PunishConfiguration punishConfiguration = plugin.getPunishConfiguration();

        if (reason.isExpire()) {
            return punishConfiguration.isOnExpire();
        }

        if (reason == UntagReason.KICK) {
            return punishConfiguration.isOnKick();
        }

        if (reason == UntagReason.QUIT) {
            return punishConfiguration.isOnDisconnect();
        }

        return false;
    }
}
