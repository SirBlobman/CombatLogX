package com.SirBlobman.expansion.placeholders.hook;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.expansion.helper.config.ConfigNewbie;

import org.bukkit.entity.Player;

final class HookNewbieHelper {
    static String getNewbieStatus(Player player) {
        if(!Expansions.isEnabled("NewbieHelper")) return null;

        boolean damagedProtected = ConfigNewbie.getData(player, "protected", false);
        return ConfigLang.get("messages.expansions.placeholder compatibility.newbie helper." + (damagedProtected ? "not " : "") + "in protection");
    }

    static String getNewbieTimeLeft(Player player) {
        if(!Expansions.isEnabled("NewbieHelper")) return null;
        boolean damagedProtected = ConfigNewbie.getData(player, "protected", false);
        if(!damagedProtected) return Integer.toString(0);

        long systemMillis = System.currentTimeMillis();
        long firstPlayed = player.getFirstPlayed();
        long subtract = (systemMillis - firstPlayed);
        int expireTime = ConfigNewbie.getOption("expire time", 30_000);

        long timeLeftMillis = Math.max((expireTime - subtract), 0);
        long timeLeftSeconds = Math.max((timeLeftMillis / 1000L), 0);
        return Long.toString(timeLeftSeconds);
    }
}