package com.SirBlobman.expansion.compatfactions.utility;

import org.bukkit.Location;

import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;

public abstract class FactionsUtil extends Util {
    public static FactionsUtil getFactionsUtil() {
        if (PluginUtil.isEnabled("Factions", "ProSavage")) return new FactionsUtilSavage();
        else if(PluginUtil.isEnabled("Factions", "drtshock")) return new FactionsUtilUUID();
        else if(PluginUtil.isEnabled("Factions")) return new FactionsUtilMassive();
        else if(PluginUtil.isEnabled("LegacyFactions")) return new FactionsUtilLegacy();

        return null;
    }

    public abstract Object getFaction(Location loc);
    public abstract boolean isSafeZone(Location loc);
}