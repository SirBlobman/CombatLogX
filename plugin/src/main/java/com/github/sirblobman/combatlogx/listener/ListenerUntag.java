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
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.listener.CombatListener;
import com.github.sirblobman.combatlogx.api.manager.IPunishManager;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import com.github.sirblobman.combatlogx.api.utility.CommandHelper;

public final class ListenerUntag extends CombatListener {
    public ListenerUntag(CombatPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        if(!isInCombat(player)) return;

        String kickReason = e.getReason();
        UntagReason untagReason = (isKickReasonIgnored(kickReason) ? UntagReason.EXPIRE : UntagReason.KICK);

        ICombatManager combatManager = getCombatManager();
        combatManager.untag(player, untagReason);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if(!isInCombat(player)) return;

        ICombatManager combatManager = getCombatManager();
        combatManager.untag(player, UntagReason.QUIT);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        LivingEntity previousEnemy = e.getPreviousEnemy();
        UntagReason untagReason = e.getUntagReason();

        ICombatLogX plugin = getCombatLogX();
        IPunishManager punishManager = plugin.getPunishManager();
        punishManager.punish(player, untagReason, previousEnemy);

        sendUntagMessage(player, untagReason);
        runUntagCommands(player, previousEnemy);
    }

    private boolean isKickReasonIgnored(String kickReason) {
        ICombatLogX plugin = getCombatLogX();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");

        List<String> kickIgnoreList = configuration.getStringList("kick-ignore-list");
        if(kickIgnoreList.isEmpty()) return false;
        return kickIgnoreList.stream().anyMatch(kickReason::contains);
    }

    private void sendUntagMessage(Player player, UntagReason untagReason) {
        if(!untagReason.isExpire()) return;
        ICombatLogX plugin = getCombatLogX();

        LanguageManager languageManager = plugin.getLanguageManager();
        String languagePath = ("combat-timer." + (untagReason == UntagReason.EXPIRE ? "expire" : "enemy-death"));
        languageManager.sendMessage(player, languagePath, null, true);
    }

    private void runUntagCommands(Player player, LivingEntity previousEnemy) {
        ConfigurationManager configurationManager = getPluginConfigurationManager();
        YamlConfiguration configuration  = configurationManager.get("commands.yml");
        List<String> untagCommandList = configuration.getStringList("untag-command-list");
        if(untagCommandList.isEmpty()) return;

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = getCombatManager();
        for(String untagCommand : untagCommandList) {
            String replacedCommand = combatManager.replaceVariables(player, previousEnemy, untagCommand);
            if(replacedCommand.startsWith("[PLAYER]")) {
                String command = replacedCommand.substring("[PLAYER]".length());
                CommandHelper.runAsPlayer(plugin, player, command);
                continue;
            }

            if(replacedCommand.startsWith("[OP]")) {
                String command = replacedCommand.substring("[OP]".length());
                CommandHelper.runAsOperator(plugin, player, command);
                continue;
            }

            CommandHelper.runAsConsole(plugin, replacedCommand);
        }
    }
}
