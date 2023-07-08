package com.github.sirblobman.combatlogx.command.combatlogx.forgive;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.language.replacer.ComponentReplacer;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;
import com.github.sirblobman.combatlogx.api.manager.IForgiveManager;
import com.github.sirblobman.combatlogx.api.object.CombatTag;
import com.github.sirblobman.combatlogx.api.placeholder.PlaceholderHelper;
import com.github.sirblobman.api.shaded.adventure.text.Component;

public final class SubCommandForgiveReject extends CombatLogPlayerCommand {
    public SubCommandForgiveReject(ICombatLogX plugin) {
        super(plugin, "reject");
        setPermissionName("combatlogx.command.combatlogx.forgive.reject");
    }

    @Override
    protected @NotNull List<String> onTabComplete(@NotNull Player player, String @NotNull [] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NotNull Player player, String @NotNull [] args) {
        ICombatLogX combatLogX = getCombatLogX();
        IForgiveManager forgiveManager = combatLogX.getForgiveManager();
        CombatTag activeRequest = forgiveManager.getActiveRequest(player);
        if (activeRequest == null) {
            sendMessage(player, "command.combatlogx.forgive.no-active-request");
            return true;
        }

        forgiveManager.removeRequest(player);

        Entity enemy = activeRequest.getEnemy();
        String playerName = player.getName();
        Component enemyName = PlaceholderHelper.getEnemyName(combatLogX, player, enemy);

        Replacer replacerEnemy = new ComponentReplacer("{enemy}", enemyName);
        Replacer replacerPlayer = new StringReplacer("{player}", playerName);
        sendMessage(player, "command.combatlogx.forgive.reject-player", replacerEnemy);
        sendMessage(enemy, "command.combatlogx.forgive.reject-enemy", replacerPlayer);
        return true;
    }
}
