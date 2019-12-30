package com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.CompatibilityWorldGuard;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

public final class HookWorldGuard {
    private enum WorldGuardVersion {
        ERROR, V6_1, V6_2, V7_0;

        public void registerFlags(CompatibilityWorldGuard expansion) {
            if(this == ERROR) {
                Logger logger = expansion.getLogger();
                logger.info("Failed to get WorldGuard version.");
                return;
            }

            String className = "com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook.HookWorldGuard_" + this.name();
            try {
                Class<?> utilClass = Class.forName(className);
                Method method = utilClass.getDeclaredMethod("registerFlags", Expansion.class);
                method.invoke(null, expansion);
            } catch(ReflectiveOperationException ex) {
                Logger logger = expansion.getLogger();
                logger.log(Level.SEVERE, "Failed to find register flags method in '" + className + "'.", ex);
            }
        }

        public void registerListeners(CompatibilityWorldGuard expansion) {
            if(this == ERROR) {
                Logger logger = expansion.getLogger();
                logger.info("Failed to get WorldGuard version.");
                return;
            }

            String className = "com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook.HookWorldGuard_" + this.name();
            try {
                Class<?> utilClass = Class.forName(className);
                Method method = utilClass.getDeclaredMethod("registerListeners", NoEntryExpansion.class);
                method.invoke(null, expansion);
            } catch(ReflectiveOperationException ex) {
                Logger logger = expansion.getLogger();
                logger.log(Level.SEVERE, "Failed to find register flags method in '" + className + "'.", ex);
            }
        }

        public boolean allowsPVP(Location location) {
            if(this == ERROR) return true;

            String className = "com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook.HookWorldGuard_" + this.name();
            try {
                Class<?> utilClass = Class.forName(className);
                Method method = utilClass.getDeclaredMethod("allowsPVP", Location.class);
                return (boolean) method.invoke(null, location);
            } catch(ReflectiveOperationException ex) {
                return true;
            }
        }

        public boolean allowsMobCombat(Location location) {
            if(this == ERROR) return true;

            String className = "com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook.HookWorldGuard_" + this.name();
            try {
                Class<?> utilClass = Class.forName(className);
                Method method = utilClass.getDeclaredMethod("allowsMobCombat", Location.class);
                return (boolean) method.invoke(null, location);
            } catch(ReflectiveOperationException ex) {
                return true;
            }
        }

        public boolean allowsTagging(Location location) {
            if(this == ERROR) return true;

            String className = "com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook.HookWorldGuard_" + this.name();
            try {
                Class<?> utilClass = Class.forName(className);
                Method method = utilClass.getDeclaredMethod("allowsTagging", Location.class);
                return (boolean) method.invoke(null, location);
            } catch(ReflectiveOperationException ex) {
                return true;
            }
        }
    }

    private static WorldGuardVersion worldGuardVersion = null;
    public static WorldGuardVersion getWorldGuardVersion() {
        if(worldGuardVersion != null) return worldGuardVersion;

        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin("WorldGuard");
        if(plugin == null) return (worldGuardVersion = WorldGuardVersion.ERROR);

        PluginDescriptionFile pdf = plugin.getDescription();
        String version = pdf.getVersion();
        if(version.startsWith("6.1")) return (worldGuardVersion = WorldGuardVersion.V6_1);
        if(version.startsWith("6.2")) return (worldGuardVersion = WorldGuardVersion.V6_2);
        if(version.startsWith("7.0")) return (worldGuardVersion = WorldGuardVersion.V7_0);

        return (worldGuardVersion = WorldGuardVersion.ERROR);
    }

    public static void registerFlags(CompatibilityWorldGuard expansion) {
        WorldGuardVersion version = getWorldGuardVersion();
        if(version == null) version = WorldGuardVersion.ERROR;
        version.registerFlags(expansion);

        Logger logger = expansion.getLogger();
        logger.info("Finished registering custom WorldGuard flags.");
    }

    public static void registerListeners(CompatibilityWorldGuard expansion) {
        WorldGuardVersion version = getWorldGuardVersion();
        if(version == null) version = WorldGuardVersion.ERROR;
        version.registerListeners(expansion);
    }

    public static boolean allowsPVP(Location location) {
        WorldGuardVersion version = getWorldGuardVersion();
        if(version == null) version = WorldGuardVersion.ERROR;

        return version.allowsPVP(location);
    }

    public static boolean allowsMobCombat(Location location) {
        WorldGuardVersion version = getWorldGuardVersion();
        if(version == null) version = WorldGuardVersion.ERROR;

        return version.allowsMobCombat(location);
    }

    public static boolean allowsTagging(Location location) {
        WorldGuardVersion version = getWorldGuardVersion();
        if(version == null) version = WorldGuardVersion.ERROR;

        return version.allowsTagging(location);
    }
}