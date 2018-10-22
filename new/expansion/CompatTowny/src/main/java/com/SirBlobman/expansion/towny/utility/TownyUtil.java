package com.SirBlobman.expansion.towny.utility;

import org.bukkit.Location;

import com.SirBlobman.combatlogx.utility.Util;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyUtil extends Util {
	public static boolean isSafeZone(Location loc) {
		TownBlock townBlock = TownyUniverse.getTownBlock(loc);
		if(townBlock != null) {
			try {
				Town town =	townBlock.getTown();
				return town.isAdminDisabledPVP();
			} catch(Throwable ex) {return false;}
		} else return false;
	}
}