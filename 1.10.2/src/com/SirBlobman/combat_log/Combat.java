package com.SirBlobman.combat_log;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.google.common.collect.Maps;

public class Combat implements Runnable
{
	private static HashMap<Player, Long> log = Maps.newHashMap();
	private static YamlConfiguration config = Config.load();
	
	private static Server S = Bukkit.getServer();
	private static ScoreboardManager SM = S.getScoreboardManager();
	private static Scoreboard MAIN = SM.getMainScoreboard();
	
	private static boolean sb = config.getBoolean("scoreboard.enabled");
	private static boolean a = config.getBoolean("options.action bar");
	private static boolean boss = config.getBoolean("options.boss bar");
	private static String prefix = Config.option("messages.prefix");
	private static String expire = prefix + Config.option("messages.expire");
	
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
				if(sb) p.setScoreboard(MAIN);
				remove(p);
				p.sendMessage(expire);
			}
			else
			{
				long end = time - System.currentTimeMillis();
				int i = (int) (end / 1000);
				if(sb) setScore(p, i);
				if(a)
				{
					String ac = Config.option("messages.action bar", i);
					CombatLog.action(p, ac);
				}
				if(boss)
				{
					bossBar(p);
				}
			}
		}
	}
	
	private void setScore(Player p, int time)
	{
		Scoreboard sb = SM.getNewScoreboard();
		Objective o = sb.registerNewObjective(Config.option("scoreboard.title"), "dummy");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score combat = o.getScore(Config.option("scoreboard.time left"));
		combat.setScore(time);
		p.setScoreboard(sb);
	}
	
	public static void add(Player... tagged)
	{
		for(Player p : tagged)
		{
			long current = System.currentTimeMillis();
			int s = config.getInt("options.timer");
			long combat = s * 1000L;
			long end = current + combat;
			log.put(p, end);
		}
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
			if(sb) {p.setScoreboard(MAIN);}
		}
	}
	
	public static boolean inCombat(Player p)
	{
		return log.containsKey(p);
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