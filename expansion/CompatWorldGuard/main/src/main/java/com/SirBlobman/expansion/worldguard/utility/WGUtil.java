package com.SirBlobman.expansion.worldguard.utility;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.worldguard.listener.ListenV6;
import com.SirBlobman.expansion.worldguard.utility.v6_1.v6_1_WGUtil;
import com.SirBlobman.expansion.worldguard.utility.v6_2.v6_2_WGUtil;
import com.SirBlobman.expansion.worldguard.utility.v7_0.ListenV7;
import com.SirBlobman.expansion.worldguard.utility.v7_0.v7_0_WGUtil;

import java.util.logging.Level;


public class WGUtil extends Util {
    private static String WorldGuardVersion;

    public static void onLoad() {
        String version = Util.PM.getPlugin("WorldGuard").getDescription().getVersion();

        if(version.startsWith("6.1")) WorldGuardVersion = "6.1";
        else if(version.startsWith("6.2")) WorldGuardVersion = "6.2";
        else if(version.startsWith("7.0")) WorldGuardVersion = "7.0";
        registerFlag();
    }
    
    public static void onEnable() {
        if(WorldGuardVersion.startsWith("6")) PluginUtil.regEvents(new ListenV6());
        else PluginUtil.regEvents(new ListenV7());
    }

    private static void registerFlag() {
        if(WorldGuardVersion.equals("6.1")) v6_1_WGUtil.registerFlag();
        else if(WorldGuardVersion.equals("6.2")) v6_2_WGUtil.registerFlag();
        else if(WorldGuardVersion.equals("7.0")) v7_0_WGUtil.registerFlag();
        else Bukkit.getLogger().log(Level.SEVERE, "Failed to load worldguard!");
    }

    public static boolean allowsPvP(Location loc) {
        switch (WorldGuardVersion) {
            case "6.1": return v6_1_WGUtil.allowsPvP(loc);
            case "6.2": return v6_2_WGUtil.allowsPvP(loc);
            case "7.0": return v7_0_WGUtil.allowsPvP(loc);
        }

        return true;
    }

    public static boolean allowsMobCombat(Location loc) {
        switch (WorldGuardVersion) {
            case "6.1": return v6_1_WGUtil.allowsMobCombat(loc);
            case "6.2": return v6_2_WGUtil.allowsMobCombat(loc);
            case "7.0": return v7_0_WGUtil.allowsMobCombat(loc);
        }
        
        return true;
    }
}