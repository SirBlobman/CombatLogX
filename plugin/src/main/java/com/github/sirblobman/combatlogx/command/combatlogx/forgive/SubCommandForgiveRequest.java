package com.github.sirblobman.combatlogx.command.combatlogx.forgive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.language.SimpleReplacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IForgiveManager;
import com.github.sirblobman.combatlogx.api.object.CombatTag;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

public final class SubCommandForgiveRequest extends CombatLogPlayerCommand {
    public SubCommandForgiveRequest(ICombatLogX plugin) {
        super(plugin, "request");
        setPermissionName("combatlogx.command.combatlogx.forgive.request");
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            List<String> valueSet = getEnemyPlayerNames(player);
            return getMatching(args[0], valueSet);
        }

        return Collections.emptyList();
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            sendMessage(player, "error.self-not-in-combat", null);
            return true;
        }

        Player target = findTarget(player, args[0]);
        if (target == null) {
            return true;
        }

        String targetName = target.getName();
        Replacer targetReplacer = new SimpleReplacer("{target}", targetName);

        CombatTag combatTag = tagInformation.getTagForEnemy(target);
        if (combatTag == null) {
            sendMessage(player, "error.forgive-not-enemy", targetReplacer);
            return true;
        }

        IForgiveManager forgiveManager = combatLogX.getForgiveManager();
        if (forgiveManager.getToggleValue(target)) {
            sendMessage(player, "error.enemy-not-forgiving", targetReplacer);
            return true;
        }

        forgiveManager.setRequest(player, combatTag);
        String playerName = player.getName();
        Replacer playerReplacer = new SimpleReplacer("{player}", playerName);
        sendMessage(player, "command.combatlogx.forgive.request-sent", targetReplacer);
        sendMessage(target, "command.combatlogx.forgive.request-receive", playerReplacer);
        return true;
    }

    private List<String> getEnemyPlayerNames(Player player) {
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return Collections.emptyList();
        }

        List<Entity> enemies = tagInformation.getEnemies();
        List<String> enemyPlayerNameList = new ArrayList<>();

        for (Entity enemy : enemies) {
            if (!(enemy instanceof Player)) {
                continue;
            }

            Player enemyPlayer = (Player) enemy;
            String enemyPlayerName = enemyPlayer.getName();
            enemyPlayerNameList.add(enemyPlayerName);
        }

        return enemyPlayerNameList;
    }
}
