package com.SirBlobman.placeholderapi;

import static com.SirBlobman.combatlogx.utility.Util.str;

import com.SirBlobman.combatlogx.*;
import com.SirBlobman.combatlogx.utility.OldUtil;

import org.bukkit.entity.*;

import me.clip.placeholderapi.external.EZPlaceholderHook;

public class HookP extends EZPlaceholderHook {
    public HookP() {super(CombatLogX.INSTANCE, "combatlogx");}

    @Override
    public String onPlaceholderRequest(Player p, String id) {
        id = id.toLowerCase();
        if(id.equals("time_left")) {
            long time = Combat.timeLeft(p);
            if(time <= 0) time = 0;
            String t = str(time);
            return t;
        } else if(id.equals("enemy_name")) {
            LivingEntity le = Combat.getEnemy(p);
            if(le != null) {
                String name = OldUtil.getName(le);
                return name;
            } else return "None";
        } else if(id.equals("enemy_health")) {
            LivingEntity le = Combat.getEnemy(p);
            if(le != null) {
                String health = OldUtil.getHealth(le);
                return health;
            } else return "None";
        } else return null;
    }
}