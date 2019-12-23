package com.SirBlobman.combatlogx.expansion.compatibility.factions.hook;

import java.util.List;

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
    public static FactionsHook getFactionsHook() {
        if(FACTIONS_HOOK != null) return FACTIONS_HOOK;

        PluginManager manager = Bukkit.getPluginManager();
        if(manager.isPluginEnabled("Factions")) {
            Plugin pluginFactions = manager.getPlugin("Factions");
            if(pluginFactions == null) return null;

            PluginDescriptionFile pdf = pluginFactions.getDescription();
            List<String> authorList = pdf.getAuthors();

            if(authorList.contains("ProSavage")) {
                FACTIONS_HOOK = new HookSavageFactions();
                return getFactionsHook();
            }

            if(authorList.contains("drtshock")) {
                FACTIONS_HOOK = new HookFactionsUUID();
                return getFactionsHook();
            }

            FACTIONS_HOOK = new HookMassiveFactions();
            return getFactionsHook();
        }

        if(manager.isPluginEnabled("LegacyFactions")) {
            FACTIONS_HOOK = new HookLegacyFactions();
            return getFactionsHook();
        }

        return null;
    }
}