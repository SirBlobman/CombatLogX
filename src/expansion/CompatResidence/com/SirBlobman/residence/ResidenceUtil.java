package com.SirBlobman.residence;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.utility.Util;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.api.ResidenceInterface;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions.FlagCombo;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;

public class ResidenceUtil extends Util {
    public static boolean canFriendlyFire(Player attacker, Player target) {
        Location tl = target.getLocation();
        ResidenceInterface ri = ResidenceApi.getResidenceManager();
        ClaimedResidence cr = ri.getByLoc(tl);
        if(cr != null) {
            ResidencePermissions rp = cr.getPermissions();
            if(rp.playerHas(target, Flags.friendlyfire, FlagCombo.OnlyTrue)) return true;
            else return false;
        } return false;
    }
}