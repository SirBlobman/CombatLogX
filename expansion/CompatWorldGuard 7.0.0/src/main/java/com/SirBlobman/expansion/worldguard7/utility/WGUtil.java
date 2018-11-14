package com.SirBlobman.expansion.worldguard7.utility;

import com.SirBlobman.combatlogx.utility.Util;

import java.lang.reflect.Method;

import org.bukkit.Location;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class WGUtil extends Util {
    private static StateFlag MOB_COMBAT = new StateFlag("mob-combat", false);

    private static WorldGuardPlatform getAPI() {
        return WorldGuard.getInstance().getPlatform();
    }

    public static void onLoad() {
        FlagRegistry fr = WorldGuard.getInstance().getFlagRegistry();
        Flag<?> flag = fr.get("mob-combat");
        if (flag != null) {
            if (flag instanceof StateFlag) {
                MOB_COMBAT = (StateFlag) flag;
            } else {
                String error = "The flag mob-combat was registered by another plugin and it is not a StateFlag! This may cause issues!";
                print(error);
            }
        } else {
            try {
                fr.register(MOB_COMBAT);
            } catch (Throwable ex1) {
                try {
                    String error = "Failed to register the flag mob-combat normally. Attempting to force-register...";
                    print(error);

                    SimpleFlagRegistry sfr = (SimpleFlagRegistry) fr;
                    Class<?> clazz = sfr.getClass();
                    Method[] methods = clazz.getDeclaredMethods();
                    boolean success = false;
                    for (Method method : methods) {
                        String name = method.getName();
                        if (name.equals("forceRegister")) {
                            method.setAccessible(true);
                            Object obj = method.invoke(sfr, new StateFlag("mob-combat", true));
                            if (obj instanceof StateFlag) {
                                MOB_COMBAT = (StateFlag) obj;
                                success = true;
                                break;
                            }
                        }
                    }

                    if (success) {
                        String msg = "Successfully registered flag mob-combat";
                        Util.print(msg);
                    } else {
                        String error1 = "Failed to force-register the flag mob-combat. This may cause issues!";
                        print(error1);
                    }
                } catch (Throwable ex2) {
                    String error = "Failed to force-register the flag mob-combat. This may cause issues!";
                    print(error);
                    ex2.printStackTrace();
                }
            }
        }
    }

    public static boolean allowsPvP(Location loc) {
        com.sk89q.worldedit.util.Location worldEditLoc = BukkitAdapter.adapt(loc);
        
        WorldGuardPlatform api = getAPI();
        RegionContainer rc = api.getRegionContainer();
        RegionQuery rq = rc.createQuery();
        
        State state = rq.queryState(worldEditLoc, null, Flags.PVP);
        return (state != State.DENY);
    }

    public static boolean allowsMobCombat(Location loc) {
        com.sk89q.worldedit.util.Location worldEditLoc = BukkitAdapter.adapt(loc);
        
        WorldGuardPlatform api = getAPI();
        RegionContainer rc = api.getRegionContainer();
        RegionQuery rq = rc.createQuery();
        
        State state = rq.queryState(worldEditLoc, null, MOB_COMBAT);
        return (state != State.DENY);
    }
}