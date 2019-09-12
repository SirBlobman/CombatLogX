package com.SirBlobman.expansion.notifier.utility;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;
import com.SirBlobman.expansion.notifier.hook.PlaceholderHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScoreboardUtil extends Util {
	private static final List<UUID> DISABLED_PLAYERS = Util.newList();

	/**
	 * Toggle if the action bar is disabled or not
	 * @param player the player to toggle
	 * @return {@code true} if enabled, {@code false} if disabled.
	 */
	public static boolean toggle(Player player) {
		UUID uuid = player.getUniqueId();

		if(DISABLED_PLAYERS.contains(uuid)) {
			DISABLED_PLAYERS.remove(uuid);
			updateScoreBoard(player);
		} else {
			DISABLED_PLAYERS.add(uuid);
			removeScoreBoard(player);
		}

		return !DISABLED_PLAYERS.contains(uuid);
	}


	private static final Map<UUID, Scoreboard> SCOREBOARDS = Util.newMap();

	public static void updateScoreBoard(Player player) {
		if(player == null) return;

		UUID uuid = player.getUniqueId();
		if(!OLD_BOARDS.containsKey(uuid)) saveOldScoreboard(player);

		Scoreboard scoreboard = SCOREBOARDS.getOrDefault(uuid, createScoreboard(player));
		SCOREBOARDS.put(uuid, scoreboard);

		Objective objective = scoreboard.getObjective("combatlogx");
		insertLines(player, scoreboard, objective);
		player.setScoreboard(scoreboard);
	}

	public static void removeScoreBoard(Player player) {
		if(player == null) return;

		UUID uuid = player.getUniqueId();
		SCOREBOARDS.remove(uuid);
		
		if(OLD_BOARDS.containsKey(uuid)) {
			restoreOldScoreboard(player);
			return;
		}

		ScoreboardManager manager = Bukkit.getScoreboardManager();
		player.setScoreboard(manager.getMainScoreboard());
	}

	private static Scoreboard createScoreboard(Player player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard scoreboard = manager.getNewScoreboard();

		String title = Util.color(ConfigNotifier.SCORE_BOARD_TITLE);
		Objective objective = NMS_Handler.getHandler().createScoreboardObjective(scoreboard, "combatlogx", "dummy", title);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		return scoreboard;
	}

	private static void insertLines(Player player, Scoreboard scoreboard, Objective objective) {
		for(String entry : scoreboard.getEntries()) scoreboard.resetScores(entry);
		
		List<String> lineList = ConfigNotifier.SCORE_BOARD_LINES;
		int index = lineList.size();
		for(String line : lineList) {
			if(Expansions.isEnabled("CompatPlaceholders")) {
				PlaceholderHandler handler = new PlaceholderHandler();
				line = handler.replaceAllPlaceholders(player, line);
			}

			line = Util.color(line);
			if(line.length() > 40) line = line.substring(0, 40);
			if(NMS_Handler.getMinorVersion() <= 7 && line.length() > 16) line = line.substring(0, 16);

			Score score = objective.getScore(line);
			score.setScore(index--);
		}
	}


	private static final Map<UUID, Scoreboard> OLD_BOARDS = Util.newMap();

	private static void saveOldScoreboard(Player player) {
		if(player == null) return;
		if(!ConfigNotifier.SCORE_BOARD_SAVE_PREVIOUS) return;

		Scoreboard scoreboard = player.getScoreboard();
		if(scoreboard == null) return;
		if(scoreboard.getObjective("combatlogx") != null) return;

		UUID uuid = player.getUniqueId();
		OLD_BOARDS.put(uuid, scoreboard);
	}

	private static void restoreOldScoreboard(Player player) {
		if(player == null) return;
		if(!ConfigNotifier.SCORE_BOARD_SAVE_PREVIOUS) return;

		UUID uuid = player.getUniqueId();
		Scoreboard scoreboard = OLD_BOARDS.getOrDefault(uuid, null);
		if(scoreboard == null) return;
		if(scoreboard.getObjective("combatlogx") != null) return;

		OLD_BOARDS.remove(uuid);
		player.setScoreboard(scoreboard);
	}
}