package com.SirBlobman.combatlogx.listener;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;
import com.SirBlobman.combatlogx.manager.CombatManager;

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
    private final CombatLogX plugin;
    public ListenerUntagger(CombatLogX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onKicked(PlayerKickEvent e) {
        String kickReason = e.getReason();
        UntagReason untagReason = (isKickReasonIgnored(kickReason) ? UntagReason.EXPIRE : UntagReason.KICK);
        
        Player player = e.getPlayer();
        ICombatManager combatManager = this.plugin.getCombatManager();
        combatManager.untag(player, untagReason);
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
        String messagePath = (untagReason == UntagReason.EXPIRE ? "combat-timer.expire" : (untagReason == UntagReason.EXPIRE_ENEMY_DEATH ? "combat-timer.enemy-death" : null));
        if(messagePath == null) return;

        ILanguageManager languageManager = this.plugin.getCombatLogXLanguageManager();
        languageManager.sendLocalizedMessage(player, messagePath);
    }

    private void runUntagCommands(Player player) {
        if(player == null) return;
        CombatManager combatManager = this.plugin.getCombatManager();
        FileConfiguration config = this.plugin.getConfig("config.yml");

        List<String> sudoCommandList = config.getStringList("untag-sudo-command-list");
        for(String sudoCommand : sudoCommandList) {
            sudoCommand = combatManager.getSudoCommand(player, null, sudoCommand);
            if(sudoCommand.startsWith("[PLAYER]")) {
                String command = sudoCommand.substring("[PLAYER]".length());
                runAsPlayer(player, command);
                continue;
            }

            if(sudoCommand.startsWith("[OP]")) {
                String command = sudoCommand.substring("[OP]".length());
                runAsOp(player, command);
                continue;
            }

            runAsConsole(sudoCommand);
        }
    }

    private void runAsConsole(String command) {
        try {
            CommandSender console = Bukkit.getConsoleSender();
            Bukkit.dispatchCommand(console, command);
        } catch(Exception ex) {
            Logger logger = this.plugin.getLogger();
            logger.log(Level.WARNING, "Failed to execute console command '" + command + "':", ex);
        }
    }

    private void runAsPlayer(Player player, String command) {
        try {
            player.performCommand(command);
        } catch(Exception ex) {
            Logger logger = this.plugin.getLogger();
            logger.log(Level.WARNING, "Failed to execute command '" + command + "' as a player:", ex);
        }
    }

    private void runAsOp(Player player, String command) {
        if(player.isOp()) {
            runAsPlayer(player, command);
            return;
        }

        try {
            player.setOp(true);
            player.performCommand(command);
        } catch(Exception ex) {
            Logger logger = this.plugin.getLogger();
            logger.log(Level.WARNING, "Failed to execute command '" + command + "' as OP:", ex);
        } finally {
            player.setOp(false);
        }
    }
    
    private boolean isKickReasonIgnored(String string) {
        FileConfiguration config = this.plugin.getConfig("config.yml");
        List<String> kickReasonIgnoreList = config.getStringList("punishments.on-kick-ignore-list");
        return kickReasonIgnoreList.stream().anyMatch(string::contains);
    }
}
