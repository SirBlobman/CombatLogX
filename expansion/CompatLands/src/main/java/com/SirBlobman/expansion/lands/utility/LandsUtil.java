package com.SirBlobman.expansion.lands.utility;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.Chunk;
import org.bukkit.Location;

import me.angeschossen.lands.api.enums.LandRole;
import me.angeschossen.lands.api.enums.LandsAction;
import me.angeschossen.lands.api.objects.LandChunk;
import me.angeschossen.lands.landsaddons.LandsAddon;

public class LandsUtil extends Util {
    private static final LandsAddon ADDON = new LandsAddon(CombatLogX.INSTANCE, false);
    private static final String KEY = ADDON.initialize();
    public static boolean isSafeZone(Location loc) {
        Chunk chunk = loc.getChunk();
        LandChunk lchunk = ADDON.getLandChunk(chunk);
        if(lchunk != null) {
            return !lchunk.getAction(LandRole.VISITOR, LandsAction.ATTACK_PLAYER);
        }
        
        return false;
    }
    
    public static void onDisable() {
        ADDON.disable(KEY);
    }
}