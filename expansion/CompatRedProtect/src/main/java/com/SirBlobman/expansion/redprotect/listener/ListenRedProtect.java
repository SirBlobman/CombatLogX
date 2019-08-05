package com.SirBlobman.expansion.redprotect.listener;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.SirBlobman.combatlogx.expansion.NoEntryExpansion.NoEntryMode;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.expansion.redprotect.CompatRedProtect;
import com.SirBlobman.expansion.redprotect.config.ConfigRedProtect;
import com.SirBlobman.expansion.redprotect.utility.RedProtectUtil;

public class ListenRedProtect implements Listener {
	private CompatRedProtect expansion;
	public ListenRedProtect(CompatRedProtect expansion) {
		this.expansion = expansion;
	}

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
	public void onCancelPVP(EntityDamageByEntityEvent e) {
		if(!e.isCancelled()) return;
		if(ConfigRedProtect.getNoEntryMode() != NoEntryMode.VULNERABLE) return;

		Entity entity = e.getEntity();
		if(!(entity instanceof Player)) return;

		Player player = (Player) entity;
		if(!CombatUtil.isInCombat(player)) return;
		if(!CombatUtil.hasEnemy(player)) return;

		LivingEntity enemy = CombatUtil.getEnemy(player);
		e.setCancelled(false);
		this.expansion.sendNoEntryMessage(player, enemy);
	}

	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if(!CombatUtil.isInCombat(player)) return;

		LivingEntity enemy = CombatUtil.getEnemy(player);
		if(enemy == null) return;

		Location toLoc = e.getTo();
		Location fromLoc = e.getFrom();

		if(!RedProtectUtil.isSafeZone(toLoc)) return;
		this.expansion.preventEntry(e, player, toLoc, fromLoc);
	}
}