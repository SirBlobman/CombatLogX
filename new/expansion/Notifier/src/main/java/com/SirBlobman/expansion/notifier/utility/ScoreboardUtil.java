package com.SirBlobman.expansion.notifier.utility;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;

public class ScoreboardUtil extends Util {
	private static final ScoreboardManager SM = SERVER.getScoreboardManager();
	private static Map<UUID, Scoreboard> SCORE_BOARDS = newMap();
	
	public static Scoreboard getScoreBoard(Player p) {
		UUID uuid = p.getUniqueId();
		if(SCORE_BOARDS.containsKey(uuid)) {
			Scoreboard sb = SCORE_BOARDS.get(uuid);
			if(sb.getObjective(p.getName()) == null) {
				String title = Util.color(ConfigNotifier.SCORE_BOARD_TITLE);
				Objective obj = sb.registerNewObjective(p.getName(), "dummy", title);
				obj.setDisplaySlot(DisplaySlot.SIDEBAR);
				SCORE_BOARDS.put(uuid, sb);
				return getScoreBoard(p);
			} return sb;
		} else {
			Scoreboard sb = SM.getNewScoreboard();
			String title = Util.color(ConfigNotifier.SCORE_BOARD_TITLE);
			Objective obj = sb.registerNewObjective(p.getName(), "dummy", title);
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			SCORE_BOARDS.put(uuid, sb);
			return getScoreBoard(p);
		}
	}
	
	public static void updateScoreBoard(Player p) {
		LivingEntity enemy = CombatUtil.getEnemy(p);
		int timeLeft = CombatUtil.getTimeLeft(p);
		if(timeLeft > 0) {
			String enemyName = (enemy != null) ? ((enemy.getCustomName() != null) ? enemy.getCustomName() : enemy.getName()) : "Unknown";
			String enemyHealth = (enemy != null) ? formatDouble(enemy.getHealth()) : "Unknown";
			
			Scoreboard sb = getScoreBoard(p);
			Objective obj = sb.getObjective(p.getName());
			if(obj != null) obj.unregister();
			
			String title = Util.color(ConfigNotifier.SCORE_BOARD_TITLE);
			obj = sb.registerNewObjective(p.getName(), "dummy", title);
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			
			List<String> scoreboardList = ConfigNotifier.SCORE_BOARD_LINES;
			int i = scoreboardList.size();
			for(String line : scoreboardList) {
				List<String> keys = Util.newList("{time_left}", "{enemy_name}", "{enemy_health}", "{in_combat}");
				List<?> vals = Util.newList(timeLeft, enemyName, enemyHealth, CombatUtil.isInCombat(p) ? "Yes" : "No");
				String msg = Util.formatMessage(line, keys, vals);
				
				Score score = obj.getScore(msg);
				score.setScore(i);
				i--;
			}
			
			if(!sb.equals(p.getScoreboard())) p.setScoreboard(sb);
		} else removeScoreBoard(p);
	}
	
	public static void removeScoreBoard(Player p) {
		Scoreboard sb = getScoreBoard(p);
		Objective obj = sb.getObjective(p.getName());
		obj.unregister();
		
		p.setScoreboard(SM.getMainScoreboard());
	}
	
	private static String formatDouble(double d) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(d);
	}
}