package com.SirBlobman.combat_log;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.SirBlobman.combat_log.utility.Util;

public class CustomScoreboard
{
	private static final Server SERVER = Bukkit.getServer();
	private static final ScoreboardManager SM = SERVER.getScoreboardManager();
	
	private Player player;
	private LivingEntity enemy;
	
	public CustomScoreboard(Player player, LivingEntity enemy)
	{
		this.player = player;
		this.enemy = enemy;
		update();
	}
	
	public void update()
	{
		String title = Config.option("scoreboard.title");
		Scoreboard sb = SM.getNewScoreboard();
		Objective combat = sb.registerNewObjective(title, "dummy");
		combat.setDisplayName(title);
		combat.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		String ehealth = "";
		String ename = "";
		if(enemy != null)
		{
			DecimalFormat df = new DecimalFormat("#.##");
			ehealth = df.format(enemy.getHealth());
			ename = enemy.getName();
		}
		int timeLeft = Combat.timeLeft(player);
				
		int i = 16;
		for(String item : Config.SCOREBOARD_LIST)
		{
			if(i <= 0) break;
			item = item.replaceAll("\\{time_left\\}", Integer.toString(timeLeft));
			item = item.replaceAll("\\{enemy_name\\}", ename);
			item = item.replaceAll("\\{enemy_health\\}", ehealth);
			item = Util.color(item);
			Score score = combat.getScore(item);
			score.setScore(i);
			i--;
		}
		
		player.setScoreboard(sb);
	}
	
	public void changeEnemy(LivingEntity newEnemy)
	{
		if(newEnemy == null) return;
		this.enemy = newEnemy;
	}
	
	public void close()
	{
		Scoreboard MAIN = SM.getMainScoreboard();
		player.setScoreboard(MAIN);
	}
}