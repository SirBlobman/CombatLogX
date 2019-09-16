package com.SirBlobman.expansion.placeholders.hook;

import java.text.DecimalFormat;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface IPlaceholderHandler {
    default String getTimeLeft(Player player) {
        int timeLeftInt = CombatUtil.getTimeLeft(player);
        String noTime = ConfigLang.get("messages.expansions.placeholder compatibility.zero time left");
        String timeString = Integer.toString(timeLeftInt);
        return (timeLeftInt > 0 ? timeString : noTime);
    }

    default String getEnemyName(Player player) {
        LivingEntity enemy = CombatUtil.getEnemy(player);
        String unknown = ConfigLang.get("messages.expansions.placeholder compatibility.unknown");
        if(enemy == null) return unknown;

        int minorVersion = NMS_Handler.getMinorVersion();
        return (minorVersion <= 7 ? enemy.getType().name() : enemy.getName());
    }

    default String getEnemyHealth(Player player) {
        LivingEntity enemy = CombatUtil.getEnemy(player);
        String unknown = ConfigLang.get("messages.expansions.placeholder compatibility.unknown");
        if(enemy == null) return unknown;

        double health = enemy.getHealth();
        DecimalFormat healthFormat = new DecimalFormat("0.00");
        return healthFormat.format(health);
    }

    default String getEnemyHealthRounded(Player player) {
        LivingEntity enemy = CombatUtil.getEnemy(player);
        String unknown = ConfigLang.get("messages.expansions.placeholder compatibility.unknown");
        if(enemy == null) return unknown;

        double health = enemy.getHealth();
        long round = Math.round(health);
        return Long.toString(round);
    }

    default String getEnemyHearts(Player player) {
        LivingEntity enemy = CombatUtil.getEnemy(player);
        String unknown = ConfigLang.get("messages.expansions.placeholder compatibility.unknown");
        if(enemy == null) return unknown;

        double health = enemy.getHealth();
        if(health > 20) return getEnemyHealthRounded(player);
        double heartsD = (health / 2.0D);

        final String heartSymbol = "\u2764";
        StringBuilder heartsBuilder = new StringBuilder("&4&l");

        int unusedHearts = 10;

        while(heartsD > 0.0D) {
            heartsD -= 1.0D;
            unusedHearts--;
            heartsBuilder.append(heartSymbol);
        }

        heartsBuilder.append("&7&l");
        while(unusedHearts > 0) {
            unusedHearts--;
            heartsBuilder.append(heartSymbol);
        }

        return Util.color(heartsBuilder.toString());
    }

    default String getInCombat(Player player) {
        String yes = ConfigLang.get("messages.expansions.placeholder compatibility.status.in combat");
        String no = ConfigLang.get("messages.expansions.placeholder compatibility.status.not in combat");
        return (CombatUtil.isInCombat(player) ? yes : no);
    }

    default String getCombatStatus(Player player) {
        String idling = ConfigLang.get("messages.expansions.placeholder compatibility.status.idling");
        String fighting = ConfigLang.get("messages.expansions.placeholder compatibility.status.fighting");
        return (CombatUtil.isInCombat(player) ? fighting : idling);
    }

    default String getNewbieStatus(Player player) {
        if(!Expansions.isEnabled("NewbieHelper")) return null;
        return HookNewbieHelper.getNewbieStatus(player);
    }

    default String getNewbieTimeLeft(Player player) {
        if(!Expansions.isEnabled("NewbieHelper")) return null;
        return HookNewbieHelper.getNewbieTimeLeft(player);
    }

    default String handlePlaceholder(Player player, String id) {
        switch(id) {
            case "time_left": return getTimeLeft(player);

            case "enemy_name": return getEnemyName(player);
            case "enemy_health": return getEnemyHealth(player);
            case "enemy_health_rounded": return getEnemyHealthRounded(player);
            case "enemy_hearts": return getEnemyHearts(player);

            case "in_combat": return getInCombat(player);
            case "status": return getCombatStatus(player);

            case "is_newbie": return getNewbieStatus(player);
            case "newbie_time_left": return getNewbieTimeLeft(player);
        }

        return null;
    }
}