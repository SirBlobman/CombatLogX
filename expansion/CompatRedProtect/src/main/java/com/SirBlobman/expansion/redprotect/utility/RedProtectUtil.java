package com.SirBlobman.expansion.redprotect.utility;

import org.bukkit.Location;

import com.SirBlobman.combatlogx.utility.Util;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import br.net.fabiozumbi12.RedProtect.Bukkit.API.RedProtectAPI;

public class RedProtectUtil extends Util {
    public static RedProtectAPI getAPI() {
        return RedProtect.get().getAPI();
    }
    
    public static boolean isSafeZone(Location loc) {
        RedProtectAPI api = getAPI();
        Region region = api.getRegion(loc);
        if(region == null) return false;
        
        boolean canPVP = region.getFlagBool("pvp");
        return !canPVP;
    }
}