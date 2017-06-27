package com.SirBlobman.combatlog.listener;

import com.SirBlobman.combatlog.Combat;
import com.SirBlobman.combatlog.config.Config;
import com.SirBlobman.combatlog.listener.event.PlayerCombatEvent;
import com.SirBlobman.combatlog.utility.Util;
import com.palmergames.bukkit.towny.db.TownyDataSource;
import com.palmergames.bukkit.towny.object.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ListenTowny implements Listener {
	@EventHandler(priority=EventPriority.HIGH)
	public void move(PlayerMoveEvent e) {
		if(e.isCancelled()) return;
		if(Config.TOWNY_PREVENT_ENTER) {
			Player p = e.getPlayer();
			Location to = e.getTo();
			boolean b1 = !pvp(to);
			boolean b2 = Combat.in(p);
			if(b1 && b2) {
				e.setCancelled(true);
				p.sendMessage(Util.color(Config.MSG_PREFIX + Config.MSG_TOWNY_NO_ENTRY));
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void pvp(PlayerCombatEvent e) {
		Player p = e.getPlayer();
		LivingEntity le = e.getAttacker();
		if(le instanceof Player) {
			Player l = (Player) le;
			if(!pvp(p)) {
				e.setCancelled(true);
				String msg = Util.color(Config.MSG_PREFIX + Config.MSG_FAIL);
				l.sendMessage(msg);
			}
		}
	}
	
	public static boolean pvp(Player p) {
		World w = p.getWorld();
		String name = w.getName();
		try {
			TownyDataSource tds = TownyUniverse.getDataSource();
			TownyWorld tw = tds.getWorld(name);
			Coord c = Coord.parseCoord(p);
			if(tw.hasTownBlock(c)) {
				TownBlock tb = tw.getTownBlock(c);
				Town t = tb.getTown();
				boolean pvp = t.isPVP();
				return pvp;
			} else return tw.isPVP();
		} catch(Exception ex) {return w.getPVP();}
	}
	
	public static boolean pvp(Location l) {
		World w = l.getWorld();
		String name = w.getName();
		try {
			TownyDataSource tds = TownyUniverse.getDataSource();
			TownyWorld tw = tds.getWorld(name);
			Coord c = Coord.parseCoord(l);
			if(tw.hasTownBlock(c)) {
				TownBlock tb = tw.getTownBlock(c);
				Town t = tb.getTown();
				boolean pvp = t.isPVP();
				return pvp;
			} else return tw.isPVP();
		} catch(Exception ex) {return w.getPVP();}
	}
}