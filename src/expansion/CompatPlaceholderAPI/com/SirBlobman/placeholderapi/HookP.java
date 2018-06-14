package com.SirBlobman.placeholderapi;

import static com.SirBlobman.combatlogx.utility.Util.str;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.utility.OldUtil;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class HookP extends PlaceholderExpansion {
    public String getAuthor() {
        return "SirBlobman";
    }

    public String getIdentifier() {
        return "combatlogx";
    }

    public String getPlugin() {
        return null;
    }

    public String getVersion() {
        return "2";
    }

    @Override
    public String onPlaceholderRequest(Player p, String id) {
        id = id.toLowerCase();
        if (id.equals("time_left")) {
            long time = Combat.timeLeft(p);
            if (time <= 0)
                time = 0;
            String t = str(time);
            return t;
        } else if (id.equals("enemy_name")) {
            LivingEntity le = Combat.getEnemy(p);
            if (le != null) {
                String name = OldUtil.getName(le);
                return name;
            } else
                return "None";
        } else if (id.equals("enemy_health")) {
            LivingEntity le = Combat.getEnemy(p);
            if (le != null) {
                String health = OldUtil.getHealth(le);
                return health;
            } else
                return "None";
        } else
            return null;
    }
}