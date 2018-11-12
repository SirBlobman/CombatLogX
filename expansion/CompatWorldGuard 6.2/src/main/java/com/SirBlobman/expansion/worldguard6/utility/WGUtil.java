package com.SirBlobman.expansion.worldguard6.utility;

import com.SirBlobman.combatlogx.utility.Util;

import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class WGUtil extends Util {
    private static StateFlag MOB_COMBAT = new StateFlag("mob-combat", false);

    private static WorldGuardPlugin getAPI() {
        return JavaPlugin.getPlugin(WorldGuardPlugin.class);
    }

    public static void onLoad() {
        WorldGuardPlugin api = getAPI();
        FlagRegistry flagRegistry = api.getFlagRegistry();
        try {
            try {
                flagRegistry.register(MOB_COMBAT);
            } catch(IllegalStateException ex) {
                try {
                    Class<?> class_SimpleFlagRegistry = SimpleFlagRegistry.class;
                    Field field_initialized = class_SimpleFlagRegistry.getDeclaredField("initialized");
                    field_initialized.setAccessible(true);
                    field_initialized.set(flagRegistry, false);
                    flagRegistry.register(MOB_COMBAT);
                    field_initialized.set(flagRegistry, true);
                    field_initialized.setAccessible(false);
                } catch(Throwable ex1) {
                    Util.print("An error occured trying to register the mob-combat flag!");
                    ex1.printStackTrace();
                }
            }
        } catch(FlagConflictException ex) {
            Util.print("The flag 'mob-combat' already exists!");
        }
    }

    private static ApplicableRegionSet getRegions(Location loc) {
        WorldGuardPlugin api = getAPI();
        
        World world = loc.getWorld();
        RegionManager rm = api.getRegionManager(world);

        return rm.getApplicableRegions(loc);
    }

    public static boolean allowsPvP(Location loc) {
        ApplicableRegionSet regions = getRegions(loc);
        State state = regions.queryState(null, DefaultFlag.PVP);
        return (state != State.DENY);
    }

    public static boolean allowsMobCombat(Location loc) {
        ApplicableRegionSet regions = getRegions(loc);
        State state = regions.queryState(null, MOB_COMBAT);
        return (state != State.DENY);
    }
}