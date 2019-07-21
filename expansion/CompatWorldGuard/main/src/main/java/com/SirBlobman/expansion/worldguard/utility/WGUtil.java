package com.SirBlobman.expansion.worldguard.utility;

import org.bukkit.Location;

import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.worldguard.listener.ListenV6;
import com.SirBlobman.expansion.worldguard.utility.v6_1.v6_1_WGUtil;
import com.SirBlobman.expansion.worldguard.utility.v6_2.v6_2_WGUtil;
import com.SirBlobman.expansion.worldguard.utility.v7_0.ListenV7;
import com.SirBlobman.expansion.worldguard.utility.v7_0.v7_0_WGUtil;


public class WGUtil extends Util {
    private static String WORLDGUARD_VERSION;

    public static void onLoad() {
        String version = Util.PM.getPlugin("WorldGuard").getDescription().getVersion();

        if(version.startsWith("6.1")) WORLDGUARD_VERSION = "6.1";
        else if(version.startsWith("6.2")) WORLDGUARD_VERSION = "6.2";
        else if(version.startsWith("7.0")) WORLDGUARD_VERSION = "7.0";
        registerFlag();
    }
    
    public static void onEnable() {
        if(WORLDGUARD_VERSION.startsWith("6")) PluginUtil.regEvents(new ListenV6());
        else PluginUtil.regEvents(new ListenV7());
    }

    private static void registerFlag() {
        if(WORLDGUARD_VERSION.equals("6.1")) {
            v6_1_WGUtil.registerMobCombatFlag();
            v6_1_WGUtil.registerNoTagFlag();
            return;
        }
        
        if(WORLDGUARD_VERSION.equals("6.2")) {
            v6_2_WGUtil.registerMobCombatFlag();
            v6_2_WGUtil.registerNoTagFlag();
            return;
        }
        
        if(WORLDGUARD_VERSION.equals("7.0")) {
            v7_0_WGUtil.registerFlags();
            return;
        }
        
        Util.print("Failed to load WorldGuard expansion! Your version does not seem to be correct!");
    }

    public static boolean allowsPvP(Location loc) {
        switch (WORLDGUARD_VERSION) {
            case "6.1": return v6_1_WGUtil.allowsPvP(loc);
            case "6.2": return v6_2_WGUtil.allowsPvP(loc);
            case "7.0": return v7_0_WGUtil.allowsPvP(loc);
        }

        return true;
    }

    public static boolean allowsMobCombat(Location loc) {
        switch (WORLDGUARD_VERSION) {
            case "6.1": return v6_1_WGUtil.allowsMobCombat(loc);
            case "6.2": return v6_2_WGUtil.allowsMobCombat(loc);
            case "7.0": return v7_0_WGUtil.allowsMobCombat(loc);
        }
        
        return true;
    }
    
    public static boolean allowsTagging(Location loc) {
        switch(WORLDGUARD_VERSION) {
        case "6.1": return v6_1_WGUtil.allowsTagging(loc);
        case "6.2": return v6_2_WGUtil.allowsTagging(loc);
        case "7.0": return v7_0_WGUtil.allowsTagging(loc);
        }
        
        return true;
    }
}