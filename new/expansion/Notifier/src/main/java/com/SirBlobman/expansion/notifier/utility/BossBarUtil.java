package com.SirBlobman.expansion.notifier.utility;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;

public class BossBarUtil extends Util {
	private static Map<UUID, BossBar> BOSS_BARS = newMap();
	
	private static BossBar getBossBar(Player p) {
		UUID uuid = p.getUniqueId();
		if(BOSS_BARS.containsKey(uuid)) {
			return BOSS_BARS.get(uuid);
		} else {
			String title = getDefaultBossBarTitle();
			BarColor color = getBossBarColor();
			BarStyle style = getBossBarStyle();
			BossBar bb = Bukkit.createBossBar(title, color, style);
			bb.setTitle(title);
			bb.addPlayer(p);
			bb.setProgress(1.0D);
			BOSS_BARS.put(uuid, bb);
			return getBossBar(p);
		}
	}
	
	private static String getDefaultBossBarTitle() {
		String format = ConfigNotifier.BOSS_BAR_FORMAT;
		List<String> keys = newList("{time_left}");
		List<?> vals = newList(ConfigOptions.OPTION_TIMER);
		return formatMessage(format, keys, vals);
	}
	
	private static BarColor getBossBarColor() {
		String scolor = ConfigNotifier.BOSS_BAR_COLOR;
		try {
			return BarColor.valueOf(scolor);
		} catch(Throwable ex) {
			String error = "Invalid color '" + scolor + "' in 'notifier.yml'/'boss bar.color'. Defaulting to YELLOW";
			Util.log(error);
			BarColor bc = BarColor.YELLOW;
			ConfigNotifier.BOSS_BAR_COLOR = BarColor.YELLOW.name();
			return bc;
		}
	}
	
	private static BarStyle getBossBarStyle() {
		String sstyle = ConfigNotifier.BOSS_BAR_STYLE;
		try {
			return BarStyle.valueOf(sstyle);
		} catch(Throwable ex) {
			String error = "Invalid style '" + sstyle + "' in 'notifier.yml/'boss bar.style'. Defaulting to SOLID";
			Util.log(error);
			BarStyle bs = BarStyle.SOLID;
			ConfigNotifier.BOSS_BAR_STYLE = BarStyle.SOLID.name();
			return bs;
		}
	}
	
	public static void updateBossBar(Player p) {
		BossBar bb = getBossBar(p);
		
		int timeLeft = CombatUtil.getTimeLeft(p);
		if(timeLeft <= 0) {
			removeBossBar(p, false);
		} else {
			List<String> keys = newList("{time_left}");
			List<?> vals = newList(timeLeft);
			String title = formatMessage(ConfigNotifier.BOSS_BAR_FORMAT, keys, vals);
			bb.setTitle(title);
			
			double dTimeLeft = (double) timeLeft;
			double dTotalTime = (double) ConfigOptions.OPTION_TIMER;
			double progress = (dTimeLeft / dTotalTime);
			if(progress <= 0) progress = 0.0D;
			if(progress >= 1) progress = 1.0D;
			
			bb.setProgress(progress);
			bb.addPlayer(p);
			
			UUID uuid = p.getUniqueId();
			BOSS_BARS.put(uuid, bb);
		}
	}
	
	public static void removeBossBar(Player p, boolean shuttingDown) {
		BossBar bb = getBossBar(p);
		
		String title = color(ConfigNotifier.BOSS_BAR_NO_LONGER_IN_COMBAT);
		bb.setTitle(title);
		
		if(shuttingDown) {
		    bb.removeAll();
		} else {
	        SchedulerUtil.runLater(20L, () -> {         
	            bb.removeAll();
	            
	            UUID uuid = p.getUniqueId();
	            BOSS_BARS.put(uuid, bb);
	        });
		}
	}
}