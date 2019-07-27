package com.SirBlobman.expansion.worldguard.utility.v6_1;

import org.bukkit.Location;
import org.bukkit.World;

import com.SirBlobman.combatlogx.utility.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class v6_1_WGUtil {
    private static WorldGuardPlugin getAPI = WorldGuardPlugin.inst();
    private static StateFlag MOB_COMBAT = new StateFlag("mob-combat", false);
    private static BooleanFlag NO_TAG = new BooleanFlag("no-tagging");

    public static void registerMobCombatFlag() {
        try {
            Field flagField = DefaultFlag.class.getField("flagsList");
            Flag<?>[] flags = new Flag[DefaultFlag.flagsList.length + 1];
            System.arraycopy(DefaultFlag.flagsList, 0, flags, 0, DefaultFlag.flagsList.length);
            flags[DefaultFlag.flagsList.length] = MOB_COMBAT;
            try {
                Field modifier = Field.class.getDeclaredField("modifiers");
                modifier.setAccessible(true);
                modifier.setInt(flagField, flagField.getModifiers() & ~Modifier.FINAL);

                flagField.set(null, flags);

            } catch (ReflectiveOperationException ex2) {
                ex2.printStackTrace();
            }
        } catch (ReflectiveOperationException ex1) {
            Util.print("&cAn error has been detected, the flag mob-combat won't work properly!");
            ex1.printStackTrace();
        }
    }
    
    public static void registerNoTagFlag() {
        try {
            Field flagsList = DefaultFlag.class.getField("flagsList");
            Flag<?>[] flags = new Flag[DefaultFlag.flagsList.length + 1];
            System.arraycopy(DefaultFlag.flagsList, 0, flags, 0, DefaultFlag.flagsList.length);
            flags[DefaultFlag.flagsList.length] = NO_TAG;
            try {
                Field modifiers = Field.class.getDeclaredField("modifiers");
                modifiers.setAccessible(true);
                modifiers.setInt(flagsList, flagsList.getModifiers() & ~Modifier.FINAL);
                flagsList.set(null, flags);
            } catch(ReflectiveOperationException ex2) {
                ex2.printStackTrace();
            }
        } catch(ReflectiveOperationException ex1) {
            Util.print("&cAn error has been detected, the flag no-tag won't work properly!");
            ex1.printStackTrace();
        }
    }

    private static ApplicableRegionSet getRegions(Location loc) {

        World world = loc.getWorld();
        RegionManager rm = getAPI.getRegionManager(world);


        return rm.getApplicableRegions(loc);
    }

    public static boolean allowsPvP(Location loc) {
        ApplicableRegionSet regionSet = getRegions(loc);
        StateFlag.State state = regionSet.queryState(null, DefaultFlag.PVP);
        return (state != StateFlag.State.DENY);
    }

    public static boolean allowsMobCombat(Location loc) {
        ApplicableRegionSet regionSet = getRegions(loc);
        StateFlag.State state = regionSet.queryValue(null, MOB_COMBAT);
        return (state != StateFlag.State.DENY);
    }
    
    public static boolean allowsTagging(Location loc) {
        ApplicableRegionSet regions = getRegions(loc);
        Boolean noTagging = regions.queryValue(null, NO_TAG);
        return (noTagging == null || !noTagging);
    }
}