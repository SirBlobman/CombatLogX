package com.SirBlobman.combatlogx.utility;

import org.bukkit.entity.*;

import java.text.DecimalFormat;

public class OldUtil extends Util {
    public static String getName(LivingEntity le) {
        if (le == null)
            return "None";
        try {
            if (le instanceof Player) {
                Player p = (Player) le;

                return p.getName();
            } else {
                return le.getName();
            }
        } catch (Throwable ex) {
            EntityType et = le.getType();

            return et.name();
        }
    }

    public static String getHealth(LivingEntity e) {
        if (e == null)
            return "None";
        try {
            double health = e.getHealth();
            DecimalFormat df = new DecimalFormat("#.##");

            return df.format(health);
        } catch (Throwable ex) {
            return "";
        }
    }
}