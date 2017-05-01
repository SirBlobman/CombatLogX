package com.SirBlobman.combatlog.utility;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class LegacyUtil extends Util {
	public static String name(LivingEntity le) {
		try {
			String name = le.getName();
			return name;
		} catch(NoSuchMethodError ex) {
			EntityType et = le.getType();
			String name = et.name();
			return name;
		}
	}
}