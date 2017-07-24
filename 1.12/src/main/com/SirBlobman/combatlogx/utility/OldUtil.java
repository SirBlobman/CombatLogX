package com.SirBlobman.combatlogx.utility;

import org.bukkit.entity.*;

import java.text.DecimalFormat;

public class OldUtil extends Util {
    public static String getName(Entity e) {
        if(e == null) return "";
        try {
            if(e instanceof Player) {
                Player p = (Player) e;
                String name = p.getName();
                return name;
            } else {
                String name = e.getName();
                return name;
            }
        } catch(Throwable ex) {
            EntityType et = e.getType();
            String name = et.name();
            return name;
        }
    }
    
    public static String getHealth(LivingEntity e) {
        if(e == null) return "";
        try {
            double health = e.getHealth();
            DecimalFormat df = new DecimalFormat("#.##");
            String f = df.format(health);
            return f;
        } catch(Throwable ex) {return "";}
    }
}