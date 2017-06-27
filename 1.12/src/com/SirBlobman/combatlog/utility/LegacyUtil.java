package com.SirBlobman.combatlog.utility;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LegacyUtil extends Util {
	public static String name(LivingEntity le) {
		try {
			if(le instanceof Player) {
				Player p = (Player) le;
				String name = p.getName();
				return name;
			} else {
				String name = le.getName();
				return name;
			}
		} catch(Throwable ex) {
			EntityType et = le.getType();
			String name = et.name();
			return name;
		}
	}
}