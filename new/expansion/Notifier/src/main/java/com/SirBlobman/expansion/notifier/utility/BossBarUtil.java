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
	
	public static BossBar getBossBar(Player p) {
		UUID uuid = p.getUniqueId();
		if(BOSS_BARS.containsKey(uuid)) {
			BossBar bb = BOSS_BARS.get(uuid);
			return bb;
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
	
	public static String getDefaultBossBarTitle() {
		String format = ConfigNotifier.BOSS_BAR_FORMAT;
		List<String> keys = newList("{time_left}");
		List<?> vals = newList(ConfigOptions.OPTION_TIMER);
		String title = formatMessage(format, keys, vals);
		return title;
	}
	
	public static BarColor getBossBarColor() {
		String scolor = ConfigNotifier.BOSS_BAR_COLOR;
		try {
			BarColor bc = BarColor.valueOf(scolor);
			if(bc == null) {
				String error = "Invalid color '" + scolor + "' in 'notifier.yml'/'boss bar.color'. Defaulting to YELLOW";
				Util.log(error);
				bc = BarColor.YELLOW;
				ConfigNotifier.BOSS_BAR_COLOR = BarColor.YELLOW.name();
			}
			return bc;
		} catch(Throwable ex) {
			String error = "Invalid color '" + scolor + "' in 'notifier.yml'/'boss bar.color'. Defaulting to YELLOW";
			Util.log(error);
			BarColor bc = BarColor.YELLOW;
			ConfigNotifier.BOSS_BAR_COLOR = BarColor.YELLOW.name();
			return bc;
		}
	}
	
	public static BarStyle getBossBarStyle() {
		String sstyle = ConfigNotifier.BOSS_BAR_STYLE;
		try {
			BarStyle bs = BarStyle.valueOf(sstyle);
			if(bs == null) {
				String error = "Invalid style '" + sstyle + "' in 'notifier.yml/'boss bar.style'. Defaulting to SOLID";
				Util.log(error);
				bs = BarStyle.SOLID;
				ConfigNotifier.BOSS_BAR_STYLE = BarStyle.SOLID.name();
			}
			return bs;
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