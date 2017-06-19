package com.SirBlobman.combatlog.compat;

import java.text.DecimalFormat;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.SirBlobman.combatlog.Combat;
import com.SirBlobman.combatlog.config.Config;
import com.SirBlobman.combatlog.utility.Util;

import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;

public class CustomScore {
	private static final Server SERVER = Bukkit.getServer();
	private static final PluginManager PM = SERVER.getPluginManager();
	private static final ScoreboardManager SM = SERVER.getScoreboardManager();
	
	private Player player;
	private LivingEntity enemy;
	
	
	public CustomScore(Player p, LivingEntity e) {
		this.player = p;
		this.enemy = e;
	}
	
	public void update() {
		if(Config.TITLE_MANAGER) {
			TitleManagerAPI api = (TitleManagerAPI) PM.getPlugin("TitleManager");
			api.removeScoreboard(player);
		}
		
		String title = Config.SCOREBOARD_TITLE;
		List<String> list = Config.SCOREBOARD_LIST;
		
		Scoreboard sb = SM.getNewScoreboard();
		Objective combat = sb.registerNewObjective(title, "dummy");
		combat.setDisplayName(Util.color(title));
		combat.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		String ehealth = "";
		String ename = "";
		if(enemy != null) {
			DecimalFormat DF = new DecimalFormat("#.##");
			ehealth = DF.format(enemy.getHealth());
			ename = enemy.getName();
		}
		int timeLeft = Combat.timeLeft(player);
		int i = 16;
		for(String s : list) {
			if(i <= 0) break;
			s = s.replace("{time_left}", Integer.toString(timeLeft));
			s = s.replace("{enemy_name}", ename);
			s = s.replace("{enemy_health}", ehealth);
			s = Util.color(s);
			if(s.length() > 40) s = s.substring(0, 39);
			Score score = combat.getScore(s);
			score.setScore(i);
			i--;
		}
		player.setScoreboard(sb);
	}
	
	public void changeEnemy(LivingEntity newEnemy) {
		if(newEnemy != null) this.enemy = newEnemy;
	}
	
	public void close() {
		Scoreboard MAIN = SM.getMainScoreboard();
		player.setScoreboard(MAIN);
		if(Config.TITLE_MANAGER) {
			TitleManagerAPI api = (TitleManagerAPI) PM.getPlugin("TitleManager");
			api.giveScoreboard(player);
		}
	}
}