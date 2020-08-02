package com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook;

import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.listener.Listener_V6_2;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;

public final class HookWorldGuard_V6_2 {
    public static WorldGuardPlugin getAPI() {
        return JavaPlugin.getPlugin(WorldGuardPlugin.class);
    }

    private static final StateFlag mobCombatFlag = new StateFlag("mob-combat", false);
    private static final BooleanFlag noTaggingFlag = new BooleanFlag("no-tagging");
    public static void registerFlags(Expansion expansion) {
        WorldGuardPlugin api = getAPI();
        FlagRegistry registry = api.getFlagRegistry();
        registry.register(mobCombatFlag);
        registry.register(noTaggingFlag);
    }

    public static void registerListeners(NoEntryExpansion expansion) {
        Listener listener = new Listener_V6_2(expansion);
        JavaPlugin plugin = expansion.getPlugin().getPlugin();
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(listener, plugin);
    }

    public static boolean allowsPVP(Location location) {
        ApplicableRegionSet regions = getRegions(location);
        StateFlag.State state = regions.queryState(null, DefaultFlag.PVP);
        return (state != StateFlag.State.DENY);
    }

    public static boolean allowsMobCombat(Location location) {
        ApplicableRegionSet regions = getRegions(location);
        StateFlag.State state = regions.queryState(null, mobCombatFlag);
        return (state != StateFlag.State.DENY);
    }

    public static boolean allowsTagging(Location location) {
        ApplicableRegionSet regions = getRegions(location);
        Boolean value = regions.queryValue(null, noTaggingFlag);
        return (value == null || !value);
    }

    private static ApplicableRegionSet getRegions(Location location) {
        World world = location.getWorld();

        WorldGuardPlugin api = getAPI();
        RegionManager manager = api.getRegionManager(world);
        return manager.getApplicableRegions(location);
    }
}