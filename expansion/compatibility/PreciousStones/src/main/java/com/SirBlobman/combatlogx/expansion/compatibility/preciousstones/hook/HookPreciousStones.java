package com.SirBlobman.combatlogx.expansion.compatibility.preciousstones.hook;

import org.bukkit.Location;

import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.api.IApi;
import net.sacredlabyrinth.Phaed.PreciousStones.field.FieldFlag;

public final class HookPreciousStones {
    public static boolean isSafeZone(Location location) {
        IApi api = PreciousStones.API();
        return api.isFieldProtectingArea(FieldFlag.PREVENT_PVP, location);
    }
}