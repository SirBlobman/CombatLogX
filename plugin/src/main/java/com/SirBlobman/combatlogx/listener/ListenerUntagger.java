package com.SirBlobman.combatlogx.listener;

import java.util.List;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenerUntagger implements Listener {
    private final ICombatLogX plugin;
    public ListenerUntagger(ICombatLogX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onKicked(PlayerKickEvent e) {
        String kickReason = e.getReason();
        if(isKickReasonIgnored(kickReason)) return;
        
        Player player = e.getPlayer();
        ICombatManager combatManager = this.plugin.getCombatManager();
        combatManager.untag(player, PlayerUntagEvent.UntagReason.KICK);
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        ICombatManager combatManager = this.plugin.getCombatManager();
        combatManager.untag(player, PlayerUntagEvent.UntagReason.QUIT);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        LivingEntity previousEnemy = e.getPreviousEnemy();
        PlayerUntagEvent.UntagReason untagReason = e.getUntagReason();

        ICombatManager combatManager = this.plugin.getCombatManager();
        combatManager.punish(player, untagReason, previousEnemy);
        sendUntagMessage(player, untagReason);
        runUntagCommands(player);
    }

    private void sendUntagMessage(Player player, PlayerUntagEvent.UntagReason untagReason) {
        if(untagReason == PlayerUntagEvent.UntagReason.EXPIRE) {
            String message = this.plugin.getLanguageMessageColoredWithPrefix("combat-timer.expire");
            this.plugin.sendMessage(player, message);
            return;
        }

        if(untagReason == PlayerUntagEvent.UntagReason.EXPIRE_ENEMY_DEATH) {
            String message = this.plugin.getLanguageMessageColoredWithPrefix("combat-timer.enemy-death");
            this.plugin.sendMessage(player, message);
        }
    }

    private void runUntagCommands(Player player) {
        if(player == null) return;

        ICombatManager combatManager = this.plugin.getCombatManager();
        FileConfiguration config = this.plugin.getConfig("config.yml");
        List<String> sudoCommandList = config.getStringList("untag-sudo-command-list");
        for(String sudoCommand : sudoCommandList) {
            sudoCommand = combatManager.getSudoCommand(player, null, sudoCommand);
            if(sudoCommand.startsWith("[PLAYER]")) {
                String command = sudoCommand.substring("[PLAYER]".length());
                player.performCommand(command);
                continue;
            }

            if(sudoCommand.startsWith("[OP]")) {
                String command = sudoCommand.substring("[OP]".length());
                if(player.isOp()) {
                    player.performCommand(command);
                    continue;
                }

                player.setOp(true);
                player.performCommand(command);
                player.setOp(false);

                continue;
            }

            CommandSender console = Bukkit.getConsoleSender();
            Bukkit.dispatchCommand(console, sudoCommand);
        }
    }
    
    private boolean isKickReasonIgnored(String string) {
        FileConfiguration config = this.plugin.getConfig("config.yml");
        List<String> kickReasonIgnoreList = config.getStringList("punishments.on-kick-ignore-list");
        return kickReasonIgnoreList.stream().anyMatch(string::contains);
    }
}