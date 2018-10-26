package com.SirBlobman.combatlogx.utility;

import org.bukkit.entity.*;

import java.text.DecimalFormat;

public class OldUtil extends Util {
    public static String getName(LivingEntity le) {
        if (le == null) return "None";
        try {
            if (le instanceof Player) {
                Player p = (Player) le;
                String name = p.getName();
                return name;
            } else {
                String name = le.getName();
                return name;
            }
        } catch (Throwable ex) {
            EntityType et = le.getType();
            String name = et.name();
            return name;
        }
    }
    
    public static String getHealth(LivingEntity e) {
        if (e == null) return "None";
        try {
            double health = e.getHealth();
            DecimalFormat df = new DecimalFormat("#.##");
            String f = df.format(health);
            return f;
        } catch (Throwable ex) {
            return "";
        }
    }
}