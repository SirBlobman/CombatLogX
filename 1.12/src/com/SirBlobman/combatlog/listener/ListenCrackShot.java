package com.SirBlobman.combatlog.listener;

import com.SirBlobman.combatlog.listener.event.PlayerCombatEvent;
import com.SirBlobman.combatlog.utility.CombatUtil;
import com.SirBlobman.combatlog.utility.Util;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.projectiles.ProjectileSource;

public class ListenCrackShot implements Listener {
	@EventHandler(priority=EventPriority.HIGHEST)
	public void dam(WeaponDamageEntityEvent e) {
		if(e.isCancelled()) return;
		double dam = e.getDamage();
		if(dam > 0) {
			Player p = e.getPlayer();
			Entity en = e.getVictim();
			Damageable le = null;
			if(en instanceof Projectile) {
				Projectile pe = (Projectile) en;
				ProjectileSource src = pe.getShooter();
				if(src instanceof Damageable) {
					le = (Damageable) src;
				} else return;
			} else {
				if(en instanceof Damageable) {le = (Damageable) en;}
				else return;
			}
			
			if(CombatUtil.canAttack(p, en)) {
				PlayerCombatEvent ce = new PlayerCombatEvent(p, le, dam, true);
				Util.callEvents(ce);
			}
		}
	}
}