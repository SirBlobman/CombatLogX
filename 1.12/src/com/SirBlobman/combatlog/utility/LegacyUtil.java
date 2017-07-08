package com.SirBlobman.combatlog.utility;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class LegacyUtil extends Util {
	public static String name(Damageable d) {
		try {
			if(d instanceof Player) {
				Player p = (Player) d;
				String name = p.getDisplayName();
				return name;
			} else {
				String name = d.getName();
				return name;
			}
		} catch(Throwable ex) {
			EntityType et = d.getType();
			if(et == null) return "UNKNOWN";
			else return et.name();
		}
	}
}