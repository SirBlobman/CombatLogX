package com.SirBlobman.combatlogx.utility;

import com.SirBlobman.combatlogx.compat.CompatEssentials;
import com.SirBlobman.combatlogx.compat.CompatTowny;
import com.SirBlobman.combatlogx.compat.factions.CompatFactions;
import com.SirBlobman.combatlogx.compat.factions.CompatFactionsLegacy;
import com.SirBlobman.combatlogx.compat.factions.CompatFactionsUUID;
import com.SirBlobman.combatlogx.compat.worldguard.CompatWorldGuard;
import com.SirBlobman.combatlogx.config.Config;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class CombatUtil extends Util {
	public static boolean canAttack(Player p, Entity e) {
		boolean pvp1 = canPvP(p);
		if(pvp1) {
			if(e instanceof Damageable) {
				Damageable d = (Damageable) e;
				if(d instanceof Player) {
					Player t = (Player) d;
					boolean pvp2 = canPvP(t);
					if(pvp2 && Config.ENABLED_FACTIONS_NORMAL) pvp2 = CompatFactions.canAttack(p, t);
					if(pvp2 && Config.ENABLED_FACTIONS_LEGACY) pvp2 = CompatFactionsLegacy.canAttack(p, t);
					if(pvp2 && Config.ENABLED_FACTIONS_UUID) pvp2 = CompatFactionsUUID.canAttack(p, t);
					if(pvp2) {
						if(!Config.OPTION_SELF_COMBAT) {
							String pname = p.getName();
							String tname = t.getName();
							if(pname.equals(tname)) return false;
							else return true;
						} else return true;
					} else return false;
				} else {
					if(Config.OPTION_MOBS_COMBAT) {
						EntityType et = d.getType();
						String type = et.name();
						List<String> list = Config.OPTION_MOBS_BLACKLIST;
						if(list.contains(type)) return false;
						else return true;
					} else return false;
				}
			} else return false;
		} else return false;
	}
	
	public static boolean canEntityAttackPlayer(Entity e, Player p) {
		if(e instanceof Player) {
			Player t = (Player) e;
			return canAttack(t, p);
		} else {
			boolean can = canPvP(p);
			if(can) {
				if(Config.OPTION_MOBS_COMBAT) {
					EntityType et = e.getType();
					String type = et.name();
					List<String> list = Config.OPTION_MOBS_BLACKLIST;
					if(list.contains(type)) return false;
					else return true;
				} else return false;
			} else return false;
		}
	}
	
	public static boolean canPvP(Player p) {
		World w = p.getWorld();
		try {
			boolean pvp = w.getPVP();
			if(pvp && Config.ENABLED_WORLD_GUARD) pvp = CompatWorldGuard.pvp(p);
			if(pvp && Config.ENABLED_TOWNY) pvp = CompatTowny.pvp(p);
			return pvp;
		} catch(Throwable ex) {
			boolean pvp = w.getPVP();
			return pvp;
		}
	}
	
	public static boolean noPvP(Location l) {
		World w = l.getWorld();
		try {
			boolean pvp = w.getPVP();
			if(pvp && Config.ENABLED_WORLD_GUARD) pvp = CompatWorldGuard.pvp(l);
			if(pvp && Config.ENABLED_TOWNY) pvp = CompatTowny.pvp(l);
			if(pvp && Config.ENABLED_FACTIONS_NORMAL) pvp = CompatFactions.pvp(l);
			if(pvp && Config.ENABLED_FACTIONS_UUID) pvp = CompatFactionsUUID.pvp(l);
			if(pvp && Config.ENABLED_FACTIONS_LEGACY) pvp = CompatFactionsLegacy.pvp(l);
			return !pvp;
		} catch(Throwable ex) {
			boolean pvp = w.getPVP();
			return pvp;
		}
	}
	
	public static boolean canBeTagged(Player p) {
		boolean can = !bypass(p);
		if(can && Config.ENABLED_ESSENTIALS) can = !CompatEssentials.hasGod(p);
		return can;
	}
	
	public static boolean bypass(Player p) {
		if(Config.OPTION_BYPASS_ENABLE) {
			String perm = Config.OPTION_BYPASS_PERMISSION;
			boolean b = p.hasPermission(perm);
			return b;
		} else return false;
	}
}