package com.SirBlobman.expansion.residence.utility;

import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.Location;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidenceManager;

public class ResidenceUtil extends Util {
    public static boolean isSafeZone(Location loc) {
        if(PluginUtil.isEnabled("Residence")) {
            ResidenceManager rm = Residence.getInstance().getResidenceManager();
            ClaimedResidence cr = rm.getByLoc(loc);
            if(cr != null) {
                boolean isPvP = cr.getPermissions().has(Flags.pvp, true);
                return !isPvP;
            }
        }
        
        return false;
    }
}