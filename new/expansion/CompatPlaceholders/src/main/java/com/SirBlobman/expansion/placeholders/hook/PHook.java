package com.SirBlobman.expansion.placeholders.hook;

import java.text.DecimalFormat;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.CombatUtil;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PHook extends PlaceholderExpansion {
	public boolean persist() {return true;}
	public String getIdentifier() {return "combatlogx";}
	public String getAuthor() {return "SirBlobman";}
	public String getVersion() {return "13.1";}
	
	@Override
	public String onPlaceholderRequest(Player player, String id) {
		switch (id) {
			case "time_left":
				int timeLeft = CombatUtil.getTimeLeft(player);
				return (timeLeft < 0 ? ConfigLang.getWithPrefix("messages.expansions.placeholder compatibility.zero time left") : Integer.toString(timeLeft));
			case "enemy_health": {
				LivingEntity enemy = CombatUtil.getEnemy(player);
				return (enemy != null) ? formatDouble(enemy.getHealth()) : "Unknown";
			}
			case "enemy_name": {
				LivingEntity enemy = CombatUtil.getEnemy(player);
				return (enemy != null) ? ((enemy.getCustomName() != null) ? enemy.getCustomName() : enemy.getName()) : "Unknown";
			}
			case "in_combat": {
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
		
		return null;
	}
	
	private static String formatDouble(double d) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(d);
	}
}