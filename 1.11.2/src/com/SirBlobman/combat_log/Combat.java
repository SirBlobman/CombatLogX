package com.SirBlobman.combat_log;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.SirBlobman.combat_log.compat.TitleManagerScoreboard;
import com.google.common.collect.Maps;

public class Combat implements Runnable
{
	private static HashMap<Player, Long> log = Maps.newHashMap();
	private static Map<Player, CustomScoreboard> scores = Maps.newHashMap();
	private static Map<Player, LivingEntity> enemies = Maps.newHashMap();
	
	@Override
	public void run()
	{
		if(log == null) return;
		if(log.isEmpty()) return;
		@SuppressWarnings("unchecked")
		Map<Player, Long> clog = (Map<Player, Long>) log.clone();
		Set<Entry<Player, Long>> set = clog.entrySet();
		if(set == null) return;
		if(set.isEmpty()) return;
		for(Entry<Player, Long> e : set)
		{
			Player p = e.getKey();
			Long time = e.getValue();
			if((time - System.currentTimeMillis()) <= 0)
			{
				if(Config.SCOREBOARD_ENABLED && scores.containsKey(p))
				{
					CustomScoreboard cs = scores.get(p);
					cs.close();
					if(Config.TITLE_MANAGER) {
						TitleManagerScoreboard TMS = CombatLog.TMS;
						TMS.reset(p);
					}
				}
				remove(p);
				String expire = Config.option("messages.prefix") + Config.option("messages.expire");
				p.sendMessage(expire);
			}
			else
			{
				long end = time - System.currentTimeMillis();
				int i = (int) (end / 1000);
				if(Config.SCOREBOARD_ENABLED) refreshScore(p);
				if(Config.ACTION_BAR)
				{
					String ac = Config.option("messages.action bar", i);
					CombatLog.action(p, ac);
				}
				if(Config.BOSS_BAR) bossBar(p);
			}
		}
	}
	
	private void refreshScore(Player p)
	{
		if(scores.containsKey(p)) {
			CustomScoreboard cs = scores.get(p);
			cs.changeEnemy(enemies.get(p));
			cs.update();
			scores.put(p, cs);
		}
		else {
			CustomScoreboard cs = new CustomScoreboard(p, enemies.get(p));
			cs.update();
			scores.put(p, cs);
		}
		
		if(Config.TITLE_MANAGER) {
			TitleManagerScoreboard TMS = CombatLog.TMS;
			TMS.custom(p);
		}
	}
	
	public static void add(Player p, LivingEntity enemy)
	{
		long current = System.currentTimeMillis();
		int s = Config.TIMER;
		long combat = s * 1000L;
		long end = current + combat;
		log.put(p, end);
		enemies.put(p, enemy);
	}
	
	public static int timeLeft(Player p)
	{
		long current = System.currentTimeMillis();
		long combat = log.containsKey(p) ? log.get(p) : 0L;
		if(combat == 0L) return 0;
		int minus = (int) ((combat - current) / 1000);
		return minus;
	}
	
	public static void remove(Player... tagged)
	{
		for(Player p : tagged)
		{
			if(log.containsKey(p)) log.remove(p);
			if(Config.SCOREBOARD_ENABLED && scores.containsKey(p))
			{
				CustomScoreboard cs = scores.get(p);
				cs.close();
				if(Config.TITLE_MANAGER) {
					TitleManagerScoreboard TMS = CombatLog.TMS;
					TMS.reset(p);
				}
				if(bosses.containsKey(p)) {
					BossBar bb = bosses.get(p);
					bb.setVisible(false);
				}
			}
		}
	}
	
	public static boolean inCombat(Player p)
	{
		boolean b = log.containsKey(p);
		return b;
	}
	
	private static Map<Player, BossBar> bosses = Maps.newHashMap();
	public static void bossBar(Player p)
	{
		if(!bosses.containsKey(p))
		{
			BossBar bb = Bukkit.createBossBar("", BarColor.RED, BarStyle.SEGMENTED_10);
			bb.addPlayer(p);
			bosses.put(p, bb);
		}

		int time = timeLeft(p);
		double div = (time / 10.0D);
		double dou = Math.min(div, 1.0D);
		String msg = Config.option("messages.boss bar", time);
		BossBar bb = bosses.get(p);
		bb.setVisible(true);
		bb.setTitle(msg);
		bb.setProgress(dou);
		if(time <= 0) bb.setVisible(false);
	}
}