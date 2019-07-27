package com.SirBlobman.expansion.towny.utility;

import com.SirBlobman.combatlogx.utility.Util;
import com.palmergames.bukkit.towny.db.TownyDataSource;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import org.bukkit.Location;
import org.bukkit.World;

public class TownyUtil extends Util {
    public static boolean isSafeZone(Location loc) {
        try {
            World world = loc.getWorld();
            String worldName = world.getName();

            TownyDataSource dataSource = TownyUniverse.getDataSource();
            TownyWorld townyWorld = dataSource.getWorld(worldName);
            Coord coord = Coord.parseCoord(loc);
            TownBlock townBlock = townyWorld.getTownBlock(coord);

            return CombatUtil.preventPvP(townyWorld, townBlock);
        } catch (NotRegisteredException ignore) {
            return false;
        }
    }
}