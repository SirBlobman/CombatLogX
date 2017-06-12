package com.SirBlobman.combatlog.listener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.SirBlobman.combatlog.Combat;
import com.SirBlobman.combatlog.config.Config;
import com.SirBlobman.combatlog.listener.event.PlayerCombatEvent;
import com.SirBlobman.combatlog.utility.Util;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;

public class ListenTowny implements Listener {
	@EventHandler(priority=EventPriority.HIGH)
	public void move(PlayerMoveEvent e) {
		if(e.isCancelled()) return;
		Player p = e.getPlayer();
		Location to = e.getTo();
		boolean b1 = !pvp(to);
		boolean b2 = Combat.in(p);
		if(b1 && b2) {
			e.setCancelled(true);
			p.sendMessage(Util.color(Config.MSG_PREFIX + Config.MSG_TOWNY_NO_ENTRY));
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void pvp(PlayerCombatEvent e) {
		Player p = e.getPlayer();
		LivingEntity le = e.getAttacker();
		Location l = p.getLocation();
		if(!pvp(l)) {
			e.setCancelled(true);
			String msg = Util.color(Config.MSG_PREFIX + Config.MSG_FAIL);
			le.sendMessage(msg);
		}
	}
	
	public static boolean pvp(Location l) {
		try {
			WorldCoord wc = WorldCoord.parseWorldCoord(l);
			TownBlock tb = wc.getTownBlock();
			Town t = tb.getTown();
			boolean pvp = t.isPVP();
			return pvp;
		} catch(Exception ex) {
			World w = l.getWorld();
			boolean pvp = w.getPVP();
			return pvp;
		}
	}
}