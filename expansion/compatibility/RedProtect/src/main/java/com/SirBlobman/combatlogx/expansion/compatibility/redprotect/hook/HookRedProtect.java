package com.SirBlobman.combatlogx.expansion.compatibility.redprotect.hook;

import org.bukkit.Location;

import br.net.fabiozumbi12.RedProtect.Bukkit.API.RedProtectAPI;
import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;

public final class HookRedProtect {
    public static RedProtectAPI getAPI() {
        RedProtect redProtect = RedProtect.get();
        return redProtect.getAPI();
    }

    public static boolean isSafeZone(Location location) {
        RedProtectAPI api = getAPI();
        Region region = api.getRegion(location);
        return (region != null && !region.getFlagBool("pvp"));
    }
}