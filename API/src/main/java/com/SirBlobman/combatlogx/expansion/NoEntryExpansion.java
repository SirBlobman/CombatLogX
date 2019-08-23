package com.SirBlobman.combatlogx.expansion;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.util.Vector;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;

public abstract class NoEntryExpansion implements CLXExpansion {
	public enum NoEntryMode {KILL, CANCEL, TELEPORT, KNOCKBACK, VULNERABLE}

	@Override
	public void enable() {
		if(!canEnable()) {
			print("Automatically disabling...");
			Expansions.unloadExpansion(this);
			return;
		}

		this.onEnable();
	}

	@Override
	public void disable() {
		// Do Nothing
	}

	private final Vector getVector(Location fromLoc, Location toLoc) {
		Vector normal = Util.getVector(fromLoc, toLoc);
		Vector multiply = normal.multiply(getKnockbackStrength());
		return Util.makeFinite(multiply);
	}

	public final void preventEntry(Cancellable e, Player player, Location toLoc, Location fromLoc) {
		if(!CombatUtil.hasEnemy(player)) return;

		LivingEntity enemy = CombatUtil.getEnemy(player);
		sendNoEntryMessage(player, enemy);

		NoEntryMode nemode = getNoEntryMode();
		if(nemode == NoEntryMode.VULNERABLE) return;

		if(nemode == NoEntryMode.CANCEL) {
			e.setCancelled(true);
			return;
		}

		if(nemode == NoEntryMode.TELEPORT) {
			player.teleport(enemy);
			return;
		}

		if(nemode == NoEntryMode.KNOCKBACK) {
			e.setCancelled(true);
			SchedulerUtil.runLater(1L, () -> {
				Vector knockback = getVector(fromLoc, toLoc);
				player.setVelocity(knockback);
			});
			return;
		}
		
		if(nemode == NoEntryMode.KILL) {
			player.setHealth(0.0D);
		}
	}

	private static List<UUID> MESSAGE_COOLDOWN = Util.newList();
	public final void sendNoEntryMessage(Player player, LivingEntity enemy) {
		if(player == null || enemy == null) return;

		UUID uuid = player.getUniqueId();
		if(MESSAGE_COOLDOWN.contains(uuid)) return;

		String message = getNoEntryMessage(!(enemy instanceof Player));
		Util.sendMessage(player, message);

		MESSAGE_COOLDOWN.add(uuid);
		long delay = (getNoEntryMessageCooldown() * 20L);
		SchedulerUtil.runLater(delay, () -> MESSAGE_COOLDOWN.remove(uuid));
	}

	public abstract boolean canEnable();
	public abstract void onEnable();
	public abstract double getKnockbackStrength();
	public abstract NoEntryMode getNoEntryMode();
	public abstract String getNoEntryMessage(boolean mobEnemy);
	public abstract int getNoEntryMessageCooldown();
}