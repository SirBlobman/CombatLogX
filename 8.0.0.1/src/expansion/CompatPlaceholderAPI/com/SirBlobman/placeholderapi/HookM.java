package com.SirBlobman.placeholderapi;

import static com.SirBlobman.combatlogx.utility.Util.str;

import com.SirBlobman.combatlogx.*;
import com.SirBlobman.combatlogx.utility.OldUtil;

import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;

import be.maximvdw.placeholderapi.*;

public class HookM implements PlaceholderReplacer {
    public void register() {
        Plugin pl = CombatLogX.INSTANCE;
        PlaceholderAPI.registerPlaceholder(pl, "combatlogx_time_left", this);
        PlaceholderAPI.registerPlaceholder(pl, "combatlogx_enemy_name", this);
        PlaceholderAPI.registerPlaceholder(pl, "combatlogx_enemy_health", this);
    }

    @Override
    public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
        Player p = e.getPlayer();
        if (p != null) {
            String id = e.getPlaceholder();
            if (id.equals("combatlogx_time_left")) {
                long time = Combat.timeLeft(p);
                if (time <= 0)
                    time = 0;
                String t = str(time);
                return t;
            } else if (id.equals("combatlogx_enemy_name")) {
                LivingEntity le = Combat.getEnemy(p);
                if (le != null) {
                    String name = OldUtil.getName(le);
                    return name;
                } else
                    return "None";
            } else if (id.equals("combatlogx_enemy_health")) {
                LivingEntity le = Combat.getEnemy(p);
                if (le != null) {
                    String health = OldUtil.getHealth(le);
                    return health;
                } else
                    return "None";
            } else
                return null;
        } else
            return null;
    }
}