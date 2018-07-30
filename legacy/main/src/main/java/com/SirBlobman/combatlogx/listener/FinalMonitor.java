package com.SirBlobman.combatlogx.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.event.PlayerPunishEvent.PunishReason;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.utility.CombatUtil;

public class FinalMonitor implements Listener {
	@EventHandler(priority=EventPriority.MONITOR)
	public void onUntag(PlayerUntagEvent e) {
		Player p = e.getPlayer();
		UntagReason uc = e.getUntagReason();
		if(uc == UntagReason.QUIT) CombatUtil.punish(p, PunishReason.DISCONNECTED);
		else if(uc == UntagReason.KICK) CombatUtil.punish(p, PunishReason.KICKED);
		else if(uc == UntagReason.EXPIRE) {
			CombatUtil.punish(p, PunishReason.UNKNOWN);
			String msg = ConfigLang.getWithPrefix("messages.combat.expire");
			p.sendMessage(msg);
		}
	}
}