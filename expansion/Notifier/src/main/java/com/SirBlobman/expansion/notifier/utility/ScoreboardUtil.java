package com.SirBlobman.expansion.notifier.utility;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.combatlogx.utility.legacy.LegacyHandler;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.text.DecimalFormat;
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
        LivingEntity enemy = CombatUtil.getEnemy(player);
        int timeLeft = CombatUtil.getTimeLeft(player);
        if (timeLeft > 0) {
            String enemyName = (enemy != null) ? ((enemy.getCustomName() != null) ? enemy.getCustomName() : enemy.getName()) : "Unknown";
            String enemyHealth = (enemy != null) ? formatDouble(enemy.getHealth()) : "Unknown";

            Scoreboard sb = getScoreBoard(player);
            Objective obj = sb.getObjective(player.getName());
            if (obj != null) obj.unregister();

            String title = Util.color(ConfigNotifier.SCORE_BOARD_TITLE);
            obj = LegacyHandler.getLegacyHandler().createScoreboardObjective(sb, player.getName(), "dummy", title);
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);

            List<String> scoreboardList = ConfigNotifier.SCORE_BOARD_LINES;
            int i = scoreboardList.size();
            for (String line : scoreboardList) {
                List<String> keys = Util.newList("{time_left}", "{enemy_name}", "{enemy_health}", "{in_combat}");
                List<?> vals = Util.newList(timeLeft, enemyName, enemyHealth, CombatUtil.isInCombat(player) ? "Yes" : "No");
                String msg = Util.formatMessage(line, keys, vals);
                if (msg.length() > 40) {
                    msg = msg.substring(0, 40);
                }

                Score score = obj.getScore(msg);
                score.setScore(i);
                i--;
            }

            if (!sb.equals(player.getScoreboard())) player.setScoreboard(sb);
        } else removeScoreBoard(player);
    }

    public static void removeScoreBoard(Player player) {
        Scoreboard sb = getScoreBoard(player);
        Objective obj = sb.getObjective(player.getName());
        obj.unregister();

        player.setScoreboard(SM.getMainScoreboard());
    }

    private static String formatDouble(double number) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(number);
    }
}