package com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook;

import java.util.logging.Logger;

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
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public final class HookWorldGuard_V7_0 {
    public static WorldGuard getAPI() {
        return WorldGuard.getInstance();
    }

    private static Flag<?> MOB_COMBAT_FLAG;
    private static Flag<?> NO_TAGGING_FLAG;
    public static void registerFlags(Expansion expansion) {
        WorldGuard api = getAPI();
        FlagRegistry registry = api.getFlagRegistry();
        
        try {
            MOB_COMBAT_FLAG = registry.get("mob-combat");
            if(MOB_COMBAT_FLAG == null) {
                MOB_COMBAT_FLAG = new StateFlag("mob-combat", false);
                registry.register(MOB_COMBAT_FLAG);
            }
            
            NO_TAGGING_FLAG = registry.get("no-tagging");
            if(NO_TAGGING_FLAG == null) {
                NO_TAGGING_FLAG = new BooleanFlag("no-tagging");
                registry.register(NO_TAGGING_FLAG);
            }
        } catch(FlagConflictException ex) {
            Logger logger = expansion.getLogger();
            logger.warning("There was a flag conflict while trying to register the custom WorldGuard flags.");
        }
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
        
        if(MOB_COMBAT_FLAG instanceof StateFlag) {
            StateFlag mobCombatFlag = (StateFlag) MOB_COMBAT_FLAG;
            State state = query.queryState(weLocation, null, mobCombatFlag);
            return (state != State.DENY);
        }
        
        return true;
    }

    public static boolean allowsTagging(Location location) {
        WorldGuard api = getAPI();
        WorldGuardPlatform platform = api.getPlatform();
        RegionContainer container = platform.getRegionContainer();
        RegionQuery query = container.createQuery();
        
        if(NO_TAGGING_FLAG instanceof BooleanFlag) {
            BooleanFlag noTaggingFlag = (BooleanFlag) NO_TAGGING_FLAG;
            com.sk89q.worldedit.util.Location weLocation = BukkitAdapter.adapt(location);
            
            Boolean value = query.queryValue(weLocation, null, noTaggingFlag);
            return (value == null || !value);
        }
        
        return true;
    }
}