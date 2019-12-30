package com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook;

import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.listener.Listener_V7_0;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public final class HookWorldGuard_V7_0 {
    public static WorldGuard getAPI() {
        return WorldGuard.getInstance();
    }

    private static final StateFlag mobCombatFlag = new StateFlag("mob-combat", false);
    private static final BooleanFlag noTaggingFlag = new BooleanFlag("no-tagging");
    public static void registerFlags(Expansion expansion) {
        WorldGuard api = getAPI();
        FlagRegistry registry = api.getFlagRegistry();
        if(registry.get("mob-combat") == null) registry.register(mobCombatFlag);
        if(registry.get("no-taggging") == null) registry.register(noTaggingFlag);
    }

    public static void registerListeners(NoEntryExpansion expansion) {
        Listener listener = new Listener_V7_0(expansion);
        JavaPlugin plugin = expansion.getPlugin().getPlugin();
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(listener, plugin);
    }

    public static boolean allowsPVP(Location location) {
        WorldGuard api = getAPI();
        WorldGuardPlatform platform = api.getPlatform();
        RegionContainer container = platform.getRegionContainer();
        RegionQuery query = container.createQuery();

        com.sk89q.worldedit.util.Location weLocation = BukkitAdapter.adapt(location);
        StateFlag.State state = query.queryState(weLocation, null, Flags.PVP);
        return (state != StateFlag.State.DENY);
    }

    public static boolean allowsMobCombat(Location location) {
        WorldGuard api = getAPI();
        WorldGuardPlatform platform = api.getPlatform();
        RegionContainer container = platform.getRegionContainer();
        RegionQuery query = container.createQuery();

        com.sk89q.worldedit.util.Location weLocation = BukkitAdapter.adapt(location);
        StateFlag.State state = query.queryState(weLocation, null, mobCombatFlag);
        return (state != StateFlag.State.DENY);
    }

    public static boolean allowsTagging(Location location) {
        WorldGuard api = getAPI();
        WorldGuardPlatform platform = api.getPlatform();
        RegionContainer container = platform.getRegionContainer();
        RegionQuery query = container.createQuery();

        com.sk89q.worldedit.util.Location weLocation = BukkitAdapter.adapt(location);
        Boolean value = query.queryValue(weLocation, null, noTaggingFlag);
        return (value == null || !value);
    }
}