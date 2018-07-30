package com.SirBlobman.expansion.notifier.utility;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;

import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarUtil extends Util {
	public static void updateActionBar(Player p) {
		int timeLeft = CombatUtil.getTimeLeft(p);
		if(timeLeft > 0) {
			List<String> keys = newList("{time_left}", "{bars_left}", "{bars_right}");
			List<?> vals = newList(timeLeft, getBarsLeft(p), getBarsRight(p));
			String msg = formatMessage(ConfigNotifier.ACTION_BAR_FORMAT, keys, vals);
			
			Spigot spigot = p.spigot();
			TextComponent tc = new TextComponent(msg);
			spigot.sendMessage(ChatMessageType.ACTION_BAR, tc);
		} else removeActionBar(p);
	}
	
	public static void removeActionBar(Player p) {
		Spigot spigot = p.spigot();
		String msg = color(ConfigNotifier.ACTION_BAR_NO_LONGER_IN_COMBAT);
		
		TextComponent tc = new TextComponent(msg);
		spigot.sendMessage(ChatMessageType.ACTION_BAR, tc);
	}
	
	public static String getBarsLeft(Player p) {
		int timeLeft = CombatUtil.getTimeLeft(p);
		int right = (ConfigOptions.OPTION_TIMER - timeLeft);
		int left = (ConfigOptions.OPTION_TIMER - right);
		
		String color = color("&a");
		for(int i = 0; i < left; i++) {
			color += "|";
		} return color;
	}
	
	public static String getBarsRight(Player p) {
		int timeLeft = CombatUtil.getTimeLeft(p);
		int right = (ConfigOptions.OPTION_TIMER - timeLeft);
		
		String color = color("&c");
		for(int i = 0; i < right; i++) {
			color += "|";
		} return color;
	}
}