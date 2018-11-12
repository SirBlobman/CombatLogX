package com.SirBlobman.expansion.worldguard.utility;

import com.SirBlobman.combatlogx.utility.Util;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import java.lang.reflect.Method;

public class WGUtil extends Util {
    private static StateFlag MOB_COMBAT = new StateFlag("mob-combat", false);

    private static WorldGuard getAPI() {
        return WorldGuard.getInstance();
    }

    public static void onLoad() {
        WorldGuard api = getAPI();
        FlagRegistry fr = api.getFlagRegistry();
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

    private static World getWorld(org.bukkit.World world) {
        return BukkitAdapter.adapt(world);
    }

    private static Vector getLocation(org.bukkit.Location loc) {
        Location loc2 = BukkitAdapter.adapt(loc);
        return loc2.toVector();
    }

    private static ApplicableRegionSet getRegions(org.bukkit.Location loc) {
        WorldGuard api = getAPI();
        WorldGuardPlatform wgp = api.getPlatform();
        RegionContainer rc = wgp.getRegionContainer();

        World world = getWorld(loc.getWorld());
        RegionManager rm = rc.get(world);

        Vector vector = getLocation(loc);
        return rm.getApplicableRegions(vector);
    }

    public static boolean allowsPvP(org.bukkit.Location loc) {
        ApplicableRegionSet regions = getRegions(loc);
        State state = regions.queryState(null, Flags.PVP);
        return (state != State.DENY);
    }

    public static boolean allowsMobCombat(org.bukkit.Location loc) {
        ApplicableRegionSet regions = getRegions(loc);
        State state = regions.queryState(null, MOB_COMBAT);
        return (state != State.DENY);
    }
}