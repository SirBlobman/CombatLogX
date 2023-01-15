package com.github.sirblobman.combatlogx.command.combatlogx.forgive;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.language.SimpleReplacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;
import com.github.sirblobman.combatlogx.api.manager.IForgiveManager;
import com.github.sirblobman.combatlogx.api.object.CombatTag;
import com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper;

public final class SubCommandForgiveReject extends CombatLogPlayerCommand {
    public SubCommandForgiveReject(ICombatLogX plugin) {
        super(plugin, "reject");
        setPermissionName("combatlogx.command.combatlogx.forgive.reject");
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        ICombatLogX combatLogX = getCombatLogX();
        IForgiveManager forgiveManager = combatLogX.getForgiveManager();
        CombatTag activeRequest = forgiveManager.getActiveRequest(player);
        if (activeRequest == null) {
            sendMessage(player, "command.combatlogx.forgive.no-active-request", null);
            return true;
        }

        forgiveManager.removeRequest(player);

        Entity enemy = activeRequest.getEnemy();
        String playerName = player.getName();
        String enemyName = PlaceholderHelper.getEnemyName(combatLogX, player, enemy);
        Replacer replacerEnemy = new SimpleReplacer("{enemy}", enemyName);
        Replacer replacerPlayer = new SimpleReplacer("{player}", playerName);
        sendMessage(player, "command.combatlogx.forgive.reject-player", replacerEnemy);
        sendMessage(enemy, "command.combatlogx.forgive.reject-enemy", replacerPlayer);
        return true;
    }
}
