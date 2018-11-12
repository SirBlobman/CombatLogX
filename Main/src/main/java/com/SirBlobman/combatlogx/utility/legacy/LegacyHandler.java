package com.SirBlobman.combatlogx.utility.legacy;

import com.SirBlobman.combatlogx.utility.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public abstract class LegacyHandler {
    public static Class<?> getInnerClass(Class<?> original, String innerClassName) {
        try {
            Class<?>[] classes = original.getClasses();
            for (Class<?> clazz : classes) {
                String name = clazz.getSimpleName();
                if (name.equals(innerClassName)) return clazz;
            }
            return null;
        } catch (Throwable ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static String getMinecraftVersion() {
        String version = Bukkit.getVersion();
        Pattern pat = Pattern.compile("(\\(MC: )([\\d\\.]+)(\\))");
        Matcher mat = pat.matcher(version);
        if (mat.find()) return mat.group(2);
        return "";
    }

    public static String baseVersion() {
        String version = getMinecraftVersion();
        int last = version.lastIndexOf('.');
        String base = (last < 2) ? version : version.substring(0, last);
        return base;
    }

    public static int getMajorVersion() {
        String baseVersion = baseVersion();
        String majorString = baseVersion.substring(0, baseVersion.indexOf("."));
        return Integer.parseInt(majorString);
    }

    public static int getMinorVersion() {
        String baseVersion = baseVersion();
        String minorString = baseVersion.substring(2);
        return Integer.parseInt(minorString);
    }
    
    private static LegacyHandler LEGACY_HANDLER = null;
    public static LegacyHandler getLegacyHandler() {
        if(LEGACY_HANDLER != null) return LEGACY_HANDLER;
        
        String mcVersion = getMinecraftVersion();
        switch(mcVersion) {
        case "1.13.2":
        case "1.13.1":
            LEGACY_HANDLER = new LegacyHandler_1_13_R2();
            break;
            
        case "1.13.0":
        case "1.13":
            LEGACY_HANDLER = new LegacyHandler_1_13_R1();
            break;
            
        case "1.12.2":
        case "1.12.1":
        case "1.12.0":
        case "1.12":
            LEGACY_HANDLER = new LegacyHandler_1_12_R1();
            break;
            
        case "1.11.2":
        case "1.11.1":
        case "1.11.0":
        case "1.11":
            LEGACY_HANDLER = new LegacyHandler_1_11_R1();
            break;
            
        case "1.10.2":
        case "1.10.1":
        case "1.10.0":
        case "1.10":
            LEGACY_HANDLER = new LegacyHandler_1_10_R1();
            break;
            
        case "1.9.4":
        case "1.9.3":
            LEGACY_HANDLER = new LegacyHandler_1_9_R2();
            break;
            
        case "1.9.2":
        case "1.9.1":
        case "1.9.0":
        case "1.9":
            LEGACY_HANDLER = new LegacyHandler_1_9_R1();
            break;
            
        case "1.8.9":
        case "1.8.8":
        case "1.8.7":
        case "1.8.6":
        case "1.8.5":
        case "1.8.4":
            LEGACY_HANDLER = new LegacyHandler_1_8_R3();
            break;
            
        case "1.8.3":
            LEGACY_HANDLER = new LegacyHandler_1_8_R2();
            break;
            
        case "1.8.2":
        case "1.8.1":
        case "1.8.0":
        case "1.8":
            LEGACY_HANDLER = new LegacyHandler_1_8_R1();
            break;
            
        default: {
            Util.print("The version " + mcVersion + " may not be supported!");
            LEGACY_HANDLER = new LegacyHandler_1_13_R2();
            break;
        }
        }
        
        return LEGACY_HANDLER;
    }

    public abstract double getMaxHealth(LivingEntity entity);

    public abstract void setMaxHealth(LivingEntity entity, double maxHealth);

    public abstract void sendActionBar(Player player, String action);

    public abstract void sendBossBar(Player player, String style, String color, String title, float progress);

    public abstract void removeBossBar(Player player);

    public abstract Objective createScoreboardObjective(Scoreboard scoreboard, String name, String criteria, String displayName);
}