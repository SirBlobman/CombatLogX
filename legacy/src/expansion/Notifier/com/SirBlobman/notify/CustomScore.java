package com.SirBlobman.notify;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.nms.NMSUtil;
import com.SirBlobman.combatlogx.utility.OldUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.notify.config.ConfigNotifier;

import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;

public class CustomScore {
    private static final ScoreboardManager SM = Util.SERVER.getScoreboardManager();
    private static Map<Player, Scoreboard> SCORE = Util.newMap();
    
    public static Scoreboard getScoreBoard(Player p) {
        if (SCORE.containsKey(p)) {
            Scoreboard sb = SCORE.get(p);
            return sb;
        } else {
            Scoreboard sb = SM.getNewScoreboard();
            Objective o = sb.registerNewObjective("CombatLogX", "dummy");
            o.setDisplayName(ConfigNotifier.SCOREBOARD_TITLE);
            o.setDisplaySlot(DisplaySlot.SIDEBAR);
            SCORE.put(p, sb);
            return getScoreBoard(p);
        }
    }
    
    public static void update(Player p) {
        if (ConfigNotifier.USE_SCOREBOARD) {
            Scoreboard sb = getScoreBoard(p);
            Objective o = sb.getObjective("CombatLogX");
            o.unregister();
            o = sb.registerNewObjective("CombatLogX", "dummy");
            String title = Util.color(ConfigNotifier.SCOREBOARD_TITLE);
            o.setDisplayName(title);
            o.setDisplaySlot(DisplaySlot.SIDEBAR);
            
            LivingEntity enemy = Combat.getEnemy(p);
            long time = Combat.timeLeft(p);
            String ename = OldUtil.getName(enemy);
            String ehealth = OldUtil.getHealth(enemy);
            List<String> l1 = Util.newList("{time_left}", "{enemy_name}", "{enemy_health}");
            List<Object> l2 = Util.newList(time, ename, ehealth);
            
            List<String> list = ConfigNotifier.SCOREBOARD_LIST;
            int i = list.size();
            for (String line : list) {
                String format = Util.formatMessage(line, l1, l2);
                format = (format.length() > getCharacterLimit()) ? format.substring(0, getCharacterLimit()) : format;
                Score s = o.getScore(format);
                s.setScore(i);
                i--;
            }
            
            if (Util.PM.isPluginEnabled("TitleManager")) {
                Plugin pl = Util.PM.getPlugin("TitleManager");
                TitleManagerAPI tm = (TitleManagerAPI) pl;
                tm.removeScoreboard(p);
            }
            
            SCORE.put(p, sb);
            p.setScoreboard(sb);
        }
    }
    
    public static void remove(Player p) {
        if (ConfigNotifier.USE_SCOREBOARD) {
            Scoreboard main = SM.getMainScoreboard();
            p.setScoreboard(main);
            SCORE.remove(p);
            if (Util.PM.isPluginEnabled("TitleManager")) {
                Plugin pl = Util.PM.getPlugin("TitleManager");
                TitleManagerAPI tm = (TitleManagerAPI) pl;
                tm.giveScoreboard(p);
            }
        }
    }
    
    public static int getCharacterLimit() {
        String mc = NMSUtil.baseVersion();
        if (mc.equals("1.7")) return 15;
        else return 39;
    }
}