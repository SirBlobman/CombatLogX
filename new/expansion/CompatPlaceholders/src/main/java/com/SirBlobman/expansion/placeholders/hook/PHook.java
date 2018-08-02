package com.SirBlobman.expansion.placeholders.hook;

import java.text.DecimalFormat;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.utility.CombatUtil;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PHook extends PlaceholderExpansion {
	public boolean persist() {return true;}
	public String getIdentifier() {return "combatlogx";}
	public String getAuthor() {return "SirBlobman";}
	public String getVersion() {return "13.1";}
	
	@Override
	public String onPlaceholderRequest(Player p, String id) {
		if(id.equals("time_left")) {
			int timeLeft = CombatUtil.getTimeLeft(p);
			if(timeLeft < 0) return "Not in combat";
			else return Integer.toString(timeLeft);
		} else if(id.equals("enemy_health")) {
			LivingEntity enemy = CombatUtil.getEnemy(p);
			String enemyHealth = (enemy != null) ? formatDouble(enemy.getHealth()) : "Unknown";
			return enemyHealth;
		} else if(id.equals("enemy_name")) {
			LivingEntity enemy = CombatUtil.getEnemy(p);
			String enemyName = (enemy != null) ? ((enemy.getCustomName() != null) ? enemy.getCustomName() : enemy.getName()) : "Unknown";
			return enemyName;
		} else return null;
	}
	
	private static String formatDouble(double d) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(d);
	}
}