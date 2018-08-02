package com.SirBlobman.expansion.placeholders.hook;

import java.text.DecimalFormat;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.utility.CombatUtil;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

public class MHook implements PlaceholderReplacer {
	public void register() {
		PlaceholderAPI.registerPlaceholder(CombatLogX.INSTANCE, "combatlogx_time_left", this);
		PlaceholderAPI.registerPlaceholder(CombatLogX.INSTANCE, "combatlogx_enemy_name", this);
		PlaceholderAPI.registerPlaceholder(CombatLogX.INSTANCE, "combatlogx_enemy_health", this);
	}
	
	@Override
	public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
		Player p = e.getPlayer();
		if(p != null) {
			String id = e.getPlaceholder();
			if(id.equals("combatlogx_time_left")) {
				int timeLeft = CombatUtil.getTimeLeft(p);
				if(timeLeft < 0) return "Not in combat";
				else return Integer.toString(timeLeft);
			} else if(id.equals("combatlogx_enemy_health")) {
				LivingEntity enemy = CombatUtil.getEnemy(p);
				String enemyHealth = (enemy != null) ? formatDouble(enemy.getHealth()) : "Unknown";
				return enemyHealth;
			} else if(id.equals("combatlogx_enemy_name")) {
				LivingEntity enemy = CombatUtil.getEnemy(p);
				String enemyName = (enemy != null) ? ((enemy.getCustomName() != null) ? enemy.getCustomName() : enemy.getName()) : "Unknown";
				return enemyName;
			} else return null;
		} else return null;
	}
	
	private static String formatDouble(double d) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(d);
	}
}