package com.github.sirblobman.combatlogx.listener;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.CombatPlugin;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import com.github.sirblobman.combatlogx.manager.CombatManager;

public class ListenerUntag extends CombatListener {
    public ListenerUntag(CombatPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onKick(PlayerKickEvent e) {
        String kickReason = e.getReason();
        UntagReason untagReason = (isKickReasonIgnored(kickReason) ? UntagReason.EXPIRE : UntagReason.KICK);
        Player player = e.getPlayer();

        CombatPlugin plugin = getPlugin();
        CombatManager combatManager = plugin.getCombatManager();
        combatManager.untag(player, untagReason);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        CombatPlugin plugin = getPlugin();
        CombatManager combatManager = plugin.getCombatManager();
        combatManager.untag(player, UntagReason.QUIT);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        LivingEntity previousEnemy = e.getPreviousEnemy();
        UntagReason untagReason = e.getUntagReason();

        CombatPlugin plugin = getPlugin();
        CombatManager combatManager = plugin.getCombatManager();
        combatManager.punish(player, untagReason, previousEnemy);

        sendUntagMessage(player, untagReason);
        runUntagCommands(player, previousEnemy);
    }

    private boolean isKickReasonIgnored(String kickReason) {
        CombatPlugin plugin = getPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");

        List<String> kickIgnoreList = configuration.getStringList("kick-ignore-list");
        if(kickIgnoreList.isEmpty()) return false;
        return kickIgnoreList.stream().anyMatch(kickReason::contains);
    }

    private void sendUntagMessage(Player player, UntagReason untagReason) {
        if(!untagReason.isExpire()) return;
        CombatPlugin plugin = getPlugin();

        LanguageManager languageManager = plugin.getLanguageManager();
        String languagePath = ("combat-timer." + (untagReason == UntagReason.EXPIRE ? "expire" : "enemy-death"));
        languageManager.sendMessage(player, languagePath, null, true);
    }

    private void runUntagCommands(Player player, LivingEntity previousEnemy) {
        CombatPlugin plugin = getPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        CombatManager combatManager = plugin.getCombatManager();
        YamlConfiguration configuration = configurationManager.get("commands.yml");

        List<String> untagCommandList = configuration.getStringList("untag-command-list");
        if(untagCommandList.isEmpty()) return;

        for(String untagCommand : untagCommandList) {
            String replacedCommand = combatManager.replaceVariables(player, previousEnemy, untagCommand);
            if(replacedCommand.startsWith("[PLAYER]")) {
                String command = replacedCommand.substring("[PLAYER]".length());
                combatManager.runAsPlayer(player, command);
                continue;
            }

            if(replacedCommand.startsWith("[OP]")) {
                String command = replacedCommand.substring("[OP]".length());
                combatManager.runAsOperator(player, command);
                continue;
            }

            combatManager.runAsConsole(replacedCommand);
        }
    }
}