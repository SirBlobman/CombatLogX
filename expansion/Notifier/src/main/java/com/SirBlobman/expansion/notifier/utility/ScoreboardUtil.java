package com.SirBlobman.expansion.notifier.utility;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.combatlogx.utility.legacy.LegacyHandler;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;
import com.SirBlobman.expansion.placeholders.hook.IPlaceholderHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScoreboardUtil extends Util {
    private static final ScoreboardManager SM = SERVER.getScoreboardManager();
    private static Map<UUID, Scoreboard> SCORE_BOARDS = newMap();
    
    private static Scoreboard getScoreBoard(Player player) {
        UUID uuid = player.getUniqueId();
        if (SCORE_BOARDS.containsKey(uuid)) {
            Scoreboard sb = SCORE_BOARDS.get(uuid);
            if (sb.getObjective(player.getName()) == null) {
                String title = Util.color(ConfigNotifier.SCORE_BOARD_TITLE);
                Objective obj = LegacyHandler.getLegacyHandler().createScoreboardObjective(sb, player.getName(), "dummy", title);
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                SCORE_BOARDS.put(uuid, sb);
                return getScoreBoard(player);
            }
            return sb;
        } else {
            Scoreboard sb = SM.getNewScoreboard();
            String title = Util.color(ConfigNotifier.SCORE_BOARD_TITLE);
            Objective obj = LegacyHandler.getLegacyHandler().createScoreboardObjective(sb, player.getName(), "dummy", title);
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            SCORE_BOARDS.put(uuid, sb);
            return getScoreBoard(player);
        }
    }
    
    public static void updateScoreBoard(Player player) {
        int timeLeftInt = CombatUtil.getTimeLeft(player);
        if (timeLeftInt > 0) {
            Scoreboard scoreBoard = getScoreBoard(player);
            Objective objective = scoreBoard.getObjective(player.getName());
            if (objective != null) objective.unregister();
            
            String title = Util.color(ConfigNotifier.SCORE_BOARD_TITLE);
            objective = LegacyHandler.getLegacyHandler().createScoreboardObjective(scoreBoard, player.getName(), "dummy", title);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            
            List<String> scoreboardList = ConfigNotifier.SCORE_BOARD_LINES;
            int i = scoreboardList.size();
            for (String line : scoreboardList) {
                if(Expansions.isEnabled("CompatPlaceholders")) {
                    IPlaceholderHandler placeholderHandler = new IPlaceholderHandler() {};
                    line = line.replace("{time_left}", placeholderHandler.handlePlaceholder(player, "time_left"))
                            .replace("{enemy_name}", placeholderHandler.handlePlaceholder(player, "enemy_name"))
                            .replace("{enemy_health}", placeholderHandler.handlePlaceholder(player, "enemy_health"))
                            .replace("{enemy_health_rounded}", placeholderHandler.handlePlaceholder(player, "enemy_health_rounded"))
                            .replace("{enemy_hearts}", placeholderHandler.handlePlaceholder(player, "enemy_hearts"))
                            .replace("{in_combat}", placeholderHandler.handlePlaceholder(player, "in_combat"))
                            .replace("{status}", placeholderHandler.handlePlaceholder(player, "status"));
                }
                
                line = color(line);
                if (line.length() > 40) line = line.substring(0, 40);
                
                Score score = objective.getScore(line);
                score.setScore(i);
                i--;
            }
            
            if (!scoreBoard.equals(player.getScoreboard())) player.setScoreboard(scoreBoard);
        } else removeScoreBoard(player);
    }
    
    public static void removeScoreBoard(Player player) {
        Scoreboard sb = getScoreBoard(player);
        Objective obj = sb.getObjective(player.getName());
        obj.unregister();
        
        player.setScoreboard(SM.getMainScoreboard());
    }
}