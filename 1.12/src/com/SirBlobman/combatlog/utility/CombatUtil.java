package com.SirBlobman.combatlog.utility;

import com.SirBlobman.combatlog.compat.CompatFactions;
import com.SirBlobman.combatlog.compat.CompatLegacyFactions;
import com.SirBlobman.combatlog.config.Config;
import com.SirBlobman.combatlog.listener.ListenTowny;

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
					if(pvp2) {
						if(!Config.SELF_COMBAT) {
							String pname = p.getName();
							String tname = t.getName();
							if(pname.equals(tname)) return false;
							else return true;
						} else return true;
					} else return false;
				} else {
					if(Config.MOBS_COMBAT) {
						EntityType et = d.getType();
						String type = et.name();
						List<String> list = Config.MOBS_BLACKLIST;
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
				if(Config.MOBS_COMBAT) {
					EntityType et = e.getType();
					String type = et.name();
					List<String> list = Config.MOBS_BLACKLIST;
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
			if(pvp && Config.ENABLED_WORLD_GUARD) pvp = WorldGuardUtil.pvp(p);
			if(pvp && Config.ENABLED_TOWNY) pvp = ListenTowny.pvp(p);
			if(pvp && Config.ENABLED_FACTIONS) pvp = CompatFactions.canPVP(p);
			if(pvp && Config.ENABLED_LEGACY_FACTIONS) pvp = CompatLegacyFactions.canPVP(p);
			return pvp;
		} catch(Throwable ex) {
			boolean pvp = w.getPVP();
			return pvp;
		}
	}
	
	public static boolean bypass(Player p) {
		if(Config.ENABLE_BYPASS) {
			String perm = Config.BYPASS_PERMISSION;
			boolean b = p.hasPermission(perm);
			return b;
		} else return false;
	}
}