package com.SirBlobman.expansion.compatfactions.util;

import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import org.bukkit.Location;

public abstract class FactionsUtil extends Util {
    public static FactionsUtil getFactionsUtil() {
        if (PluginUtil.isEnabled("Factions", "ProSavage")) return new FactionsUtilSavage();

        else return null;
    }

    public abstract Object getFaction(Location loc);

    public abstract boolean isSafeZone(Location loc);
}