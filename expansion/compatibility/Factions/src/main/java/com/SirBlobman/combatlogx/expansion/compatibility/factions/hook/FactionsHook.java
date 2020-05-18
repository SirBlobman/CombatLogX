package com.SirBlobman.combatlogx.expansion.compatibility.factions.hook;

import java.util.List;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.expansion.compatibility.factions.CompatibilityFactions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

public abstract class FactionsHook {
    public abstract Object getFactionAt(Location location);
    public abstract Object getFactionFor(OfflinePlayer offline);
    public abstract boolean isSafeZone(Location location);

    public Object getFactionAt(Entity entity) {
        if(entity == null) return null;

        Location location = entity.getLocation();
        return getFactionAt(location);
    }

    private static FactionsHook FACTIONS_HOOK = null;
    public static FactionsHook getFactionsHook(CompatibilityFactions expansion) {
        if(FACTIONS_HOOK != null) return FACTIONS_HOOK;

        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = expansion.getLogger();

        if(manager.isPluginEnabled("Factions")) {
            Plugin pluginFactions = manager.getPlugin("Factions");
            if(pluginFactions == null) return null;

            PluginDescriptionFile pdf = pluginFactions.getDescription();
            List<String> authorList = pdf.getAuthors();
            String version = pdf.getVersion();

            if(version.startsWith("1.6.9.5-U") || authorList.contains("drtshock")) {
                FACTIONS_HOOK = new HookFactionsUUID();
                logger.info("Successfully hooked into FactionsUUID v" + version);
                return getFactionsHook(expansion);
            }

            FACTIONS_HOOK = new HookMassiveFactions();
            logger.info("Successfully hooked into Factions v" + version);
            return getFactionsHook(expansion);
        }

        if(manager.isPluginEnabled("LegacyFactions")) {
            Plugin pluginLegacyFactions = manager.getPlugin("LegacyFactions");
            if(pluginLegacyFactions == null) return null;

            PluginDescriptionFile pdf = pluginLegacyFactions.getDescription();
            String version = pdf.getVersion();

            FACTIONS_HOOK = new HookLegacyFactions();
            logger.info("Successfully hooked into LegacyFactions v" + version);
            return getFactionsHook(expansion);
        }
        
        if(manager.isPluginEnabled("FactionsX")) {
            Plugin pluginFactionsX = manager.getPlugin("FactionsX");
            if(pluginFactionsX == null) return null;
            
            PluginDescriptionFile pdf = pluginFactionsX.getDescription();
            String version = pdf.getVersion();
            
            FACTIONS_HOOK = new HookFactionsX();
            logger.info("Successfully hooked into FactionsX v" + version);
            return getFactionsHook(expansion);
        }

        return null;
    }
}