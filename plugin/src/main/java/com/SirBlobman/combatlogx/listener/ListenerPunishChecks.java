package com.SirBlobman.combatlogx.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPunishEvent;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ListenerPunishChecks implements Listener {
    private final ICombatLogX plugin;
    public ListenerPunishChecks(ICombatLogX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void beforePunish(PlayerPunishEvent e) {
        FileConfiguration config = this.plugin.getConfig("config.yml");
        boolean punishOnQuit = config.getBoolean("punishments.on-quit");
        boolean punishOnKick = config.getBoolean("punishments.on-kick");
        boolean punishOnExpire = config.getBoolean("punishments.on-expire");

        PlayerUntagEvent.UntagReason punishReason = e.getPunishReason();
        if(punishReason.isExpire() && !punishOnExpire) {
            e.setCancelled(true);
            return;
        }

        if(punishReason == PlayerUntagEvent.UntagReason.KICK && !punishOnKick) {
            e.setCancelled(true);
            return;
        }

        if(punishReason == PlayerUntagEvent.UntagReason.QUIT && !punishOnQuit) {
            e.setCancelled(true);
            // return;
        }
    }
}