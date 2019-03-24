package com.SirBlobman.expansion.lands.utility;

import org.bukkit.Chunk;
import org.bukkit.Location;

import com.SirBlobman.combatlogx.utility.Util;

import me.angeschossen.lands.api.enums.LandRole;
import me.angeschossen.lands.api.enums.LandsAction;
import me.angeschossen.lands.api.landsaddons.LandsAddon;
import me.angeschossen.lands.api.objects.LandChunk;

public class LandsUtil extends Util {
    private static LandsAddon ADDON = null;
    private static String KEY = null;
    
    public static LandsAddon getAddon() {
        if(ADDON == null) {
            ADDON = new LandsAddon(PLUGIN, false);
        }
        
        if(!ADDON.isEnabled()) {
            KEY = ADDON.initialize();
        }
        
        return ADDON;
    }
    
    public static boolean isSafeZone(Location loc) {
        Chunk chunk = loc.getChunk();
        
        LandsAddon addon = getAddon();
        LandChunk lchunk = addon.getLandChunk(chunk);
        if(lchunk != null) {
            return !lchunk.getAction(LandRole.VISITOR, LandsAction.ATTACK_PLAYER);
        }
        
        return false;
    }
    
    public static void onDisable() {
        if(ADDON == null || KEY == null) return;
        
        getAddon().disable(KEY);
    }
}