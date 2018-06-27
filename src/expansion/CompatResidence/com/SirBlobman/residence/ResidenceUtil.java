package com.SirBlobman.residence;

import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.utility.Util;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.api.ResidenceInterface;

public class ResidenceUtil extends Util {
    public static ResidenceInterface getInterface() {
        ResidenceInterface ri = ResidenceApi.getResidenceManager();
        return ri;
    }

    public static boolean canFriendlyFire(Player attacker, Player target) {
        return false;
    }
}