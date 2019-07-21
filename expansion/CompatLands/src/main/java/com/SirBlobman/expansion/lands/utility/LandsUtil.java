package com.SirBlobman.expansion.lands.utility;

import org.bukkit.Chunk;
import org.bukkit.Location;

import com.SirBlobman.combatlogx.utility.Util;

import java.lang.reflect.Method;

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
    
    public static boolean isSafeZone(Location location) {
        try {
            Class<?> class_LandsAddon = Class.forName("me.angeschossen.lands.api.landsaddons.LandsAddon");
            Method method_getLandChunk = class_LandsAddon.getDeclaredMethod("getLandChunk", Location.class);
            LandChunk landChunk = (LandChunk) method_getLandChunk.invoke(getAddon(), location);
            
            if(landChunk == null) return false;
            return !landChunk.getAction(LandRole.VISITOR, LandsAction.ATTACK_PLAYER);
        } catch(ReflectiveOperationException error1) {
            try {
                Chunk chunk = location.getChunk();
                
                Class<?> class_LandsAddon = Class.forName("me.angeschossen.lands.api.landsaddons.LandsAddon");
                Method method_getLandChunk = class_LandsAddon.getDeclaredMethod("getLandChunk", Chunk.class);
                LandChunk landChunk = (LandChunk) method_getLandChunk.invoke(getAddon(), chunk);
                
                if(landChunk == null) return false;
                return !landChunk.getAction(LandRole.VISITOR, LandsAction.ATTACK_PLAYER);
            } catch(ReflectiveOperationException error2) {
                Util.log("[Lands Compatibility] Could not detect safezone for Lands plugin, please contact SirBlobman!" );
                error2.printStackTrace();
                return false;
            }
        }
    }
    
    public static void onDisable() {
        if(ADDON == null || KEY == null) return;
        
        getAddon().disable(KEY);
    }
}