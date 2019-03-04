package com.SirBlobman.expansion.worldguard.utility.v7_0;

import com.SirBlobman.combatlogx.utility.Util;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class v7_0_WGUtil extends Util {

    private static final StateFlag MOB_COMBAT = new StateFlag("mob-combat", false);

    private static WorldGuardPlatform getAPI() {
        return WorldGuard.getInstance().getPlatform();
    }


    public static void registerFlag() {
        FlagRegistry fr = WorldGuard.getInstance().getFlagRegistry();
        if(fr.get("mob-combat") != null) {
        } else fr.register(MOB_COMBAT);
    }

    public static boolean allowsPvP(Location loc) {
        com.sk89q.worldedit.util.Location worldEditLoc = BukkitAdapter.adapt(loc);

        WorldGuardPlatform api = getAPI();
        RegionContainer rc = api.getRegionContainer();

        for(ProtectedRegion region : rc.createQuery().getApplicableRegions(worldEditLoc).getRegions()) {

            List<Flag> flags = new ArrayList<>(region.getFlags().keySet());
            List<Object> values = new ArrayList<>(region.getFlags().values());

            for (int i = 0; i < flags.size(); i++) {
                String flagName = flags.get(i).getName();
                if (flagName.equals("pvp")) {
                    if(values.get(i) instanceof StateFlag.State) {
                        StateFlag.State state = (StateFlag.State) values.get(i);
                        if(state == null) return true;
                        return state == StateFlag.State.ALLOW;
                    }
                }
            }
        }
        return true;
    }

    public static boolean allowsMobCombat(Location loc) {
        com.sk89q.worldedit.util.Location worldEditLoc = BukkitAdapter.adapt(loc);

        WorldGuardPlatform api = getAPI();
        RegionContainer rc = api.getRegionContainer();
        RegionQuery rq = rc.createQuery();

        StateFlag.State state = rq.queryState(worldEditLoc, null, MOB_COMBAT);
        if(state == null) return true;
        return state != StateFlag.State.DENY;
    }

}
