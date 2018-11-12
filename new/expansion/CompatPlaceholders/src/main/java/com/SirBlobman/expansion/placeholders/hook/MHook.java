package com.SirBlobman.expansion.placeholders.hook;

import java.text.DecimalFormat;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.CombatUtil;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

public class MHook implements PlaceholderReplacer {
	public void register() {
		PlaceholderAPI.registerPlaceholder(CombatLogX.INSTANCE, "combatlogx_time_left", this);
		PlaceholderAPI.registerPlaceholder(CombatLogX.INSTANCE, "combatlogx_enemy_name", this);
		PlaceholderAPI.registerPlaceholder(CombatLogX.INSTANCE, "combatlogx_enemy_health", this);
		PlaceholderAPI.registerPlaceholder(CombatLogX.INSTANCE, "combatlogx_in_combat", this);
	}
	
	@Override
	public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
		Player player = e.getPlayer();
		if(player != null) {
			String id = e.getPlaceholder();
			switch (id) {
				case "combatlogx_time_left":
					int timeLeft = CombatUtil.getTimeLeft(player);
					return (timeLeft < 0 ? ConfigLang.getWithPrefix("messages.expansions.placeholder compatibility.zero time left") : Integer.toString(timeLeft));
				case "combatlogx_enemy_health": {
					LivingEntity enemy = CombatUtil.getEnemy(player);
					return (enemy != null) ? formatDouble(enemy.getHealth()) : "Unknown";
				}
				case "combatlogx_enemy_name": {
					LivingEntity enemy = CombatUtil.getEnemy(player);
					return (enemy != null) ? ((enemy.getCustomName() != null) ? enemy.getCustomName() : enemy.getName()) : "Unknown";
				}
				case "combatlogx_in_combat": {
                    String yes = ConfigLang.get("messages.expansions.placeholder comptability.yes");
                    String no = ConfigLang.get("messages.expansions.placeholder comptability.no");
                    return CombatUtil.isInCombat(player) ? yes : no;
				}
				case "combatlogx_status": {
                    String idling = ConfigLang.get("messages.expansions.placeholder comptability.status.idling");
                    String fighting = ConfigLang.get("messages.expansions.placeholder comptability.status.fighting");
                    return CombatUtil.isInCombat(player) ? fighting : idling;
				}
			}
		}
		
		return null;
	}
	
	private static String formatDouble(double d) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(d);
	}
}