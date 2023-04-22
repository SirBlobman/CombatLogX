package com.github.sirblobman.combatlogx.command.combatlogx.forgive;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.language.replacer.ComponentReplacer;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IForgiveManager;
import com.github.sirblobman.combatlogx.api.object.CombatTag;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import com.github.sirblobman.combatlogx.api.placeholder.PlaceholderHelper;

import org.jetbrains.annotations.NotNull;

public final class SubCommandForgiveAccept extends CombatLogPlayerCommand {
    public SubCommandForgiveAccept(@NotNull ICombatLogX plugin) {
        super(plugin, "accept");
        setPermissionName("combatlogx.command.combatlogx.forgive.accept");
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

        Entity enemy = activeRequest.getEnemy();
        if (enemy == null) {
            sendMessage(player, "command.combatlogx.forgive.no-active-request");
            return true;
        }

        ICombatManager combatManager = combatLogX.getCombatManager();
        combatManager.untag(player, enemy, UntagReason.ENEMY_FORGIVE);

        String playerName = player.getName();
        Component enemyName = PlaceholderHelper.getEnemyName(combatLogX, player, enemy);

        Replacer enemyNameReplacer = new ComponentReplacer("{enemy}", enemyName);
        Replacer playerNameReplacer = new StringReplacer("{player}", playerName);
        sendMessage(player, "command.combatlogx.forgive.accept-player", enemyNameReplacer);
        sendMessage(enemy, "command.combatlogx.forgive.accept-enemy", playerNameReplacer);
        return true;
    }
}
