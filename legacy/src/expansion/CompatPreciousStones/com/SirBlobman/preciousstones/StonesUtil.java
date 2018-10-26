package com.SirBlobman.preciousstones;

import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.api.IApi;
import net.sacredlabyrinth.Phaed.PreciousStones.field.FieldFlag;

public class StonesUtil extends Util {
    public static boolean canPvP(Player p) {
        Location l = p.getLocation();
        boolean pvp = canPvP(l);
        return pvp;
    }
    
    public static boolean canPvP(Location l) {
        IApi api = PreciousStones.API();
        boolean pvp = api.isFieldProtectingArea(FieldFlag.PREVENT_PVP, l);
        return pvp;
    }
}