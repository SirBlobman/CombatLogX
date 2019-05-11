package com.SirBlobman.expansion.notifier.utility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;

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
    
    private static Map<UUID, Scoreboard> SCORE_BOARDS = newMap();
    
    private static Scoreboard getScoreBoard(Player player) {
        UUID uuid = player.getUniqueId();
        if(DISABLED_PLAYERS.contains(uuid)) return null;
        
        if (SCORE_BOARDS.containsKey(uuid)) {
            Scoreboard sb = SCORE_BOARDS.get(uuid);
            if (sb.getObjective(player.getName()) == null) {
                String title = Util.color(ConfigNotifier.SCORE_BOARD_TITLE);
                Objective obj = NMS_Handler.getHandler().createScoreboardObjective(sb, player.getName(), "dummy", title);
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                SCORE_BOARDS.put(uuid, sb);
                return getScoreBoard(player);
            }
            return sb;
        } else {
            Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
            String title = Util.color(ConfigNotifier.SCORE_BOARD_TITLE);
            Objective obj = NMS_Handler.getHandler().createScoreboardObjective(sb, player.getName(), "dummy", title);
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            SCORE_BOARDS.put(uuid, sb);
            return getScoreBoard(player);
        }
    }
    
    public static void updateScoreBoard(Player player) {
        UUID uuid = player.getUniqueId();
        if(DISABLED_PLAYERS.contains(uuid)) return;
        
        int timeLeftInt = CombatUtil.getTimeLeft(player);
        if(timeLeftInt <= 0) {
            removeScoreBoard(player);
            return;
        }
        
        Scoreboard scoreBoard = getScoreBoard(player);
        Objective objective = scoreBoard.getObjective(player.getName());
        if (objective != null) objective.unregister();
        
        String title = Util.color(ConfigNotifier.SCORE_BOARD_TITLE);
        objective = NMS_Handler.getHandler().createScoreboardObjective(scoreBoard, player.getName(), "dummy", title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        List<String> scoreboardList = ConfigNotifier.SCORE_BOARD_LINES;
        int i = scoreboardList.size();
        for (String line : scoreboardList) {
            if(Expansions.isEnabled("CompatPlaceholders")) {
                PlaceholderHandler handler = new PlaceholderHandler();
                line = handler.replaceAllPlaceholders(player, line);
            }
            
            line = color(line);
            if (line.length() > 40) line = line.substring(0, 40);
            
            Score score = objective.getScore(line);
            score.setScore(i);
            i--;
        }
        
        if (!scoreBoard.equals(player.getScoreboard())) player.setScoreboard(scoreBoard);
    }
    
    public static void removeScoreBoard(Player player) {
        Scoreboard sb = getScoreBoard(player);
        if(sb != null) {
            Objective obj = sb.getObjective(player.getName());
            obj.unregister();
        }
        
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }
}