package com.SirBlobman.combatlogx.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.utility.CombatUtil;

public class PunishListener implements Listener {
	@EventHandler(priority=EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		CombatUtil.untag(p, UntagReason.QUIT);
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onKick(PlayerKickEvent e) {
		Player p = e.getPlayer();
		CombatUtil.untag(p, UntagReason.KICK);
	}
}