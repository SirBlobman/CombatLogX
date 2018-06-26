package com.SirBlobman.residence;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.utility.Util;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.api.ResidenceInterface;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public class ResidenceUtil extends Util {
    public static ResidenceInterface getInterface() {
        ResidenceInterface ri = ResidenceApi.getResidenceManager();
        return ri;
    }

    public static boolean canFriendlyFire(Player attacker, Player target) {
        ResidenceInterface ri = getInterface();
        Location loc = target.getLocation();
        ClaimedResidence cr = ri.getByLoc(loc);
        if(cr != null) {
            List<Player> inResidence = cr.getPlayersInResidence();
            if(inResidence.contains(attacker) && inResidence.contains(target)) {
                cr.getPermissions().listFlags();
            } else return false;
        } else {
            loc = attacker.getLocation();
            cr = ri.getByLoc(loc);
        }
    }
}