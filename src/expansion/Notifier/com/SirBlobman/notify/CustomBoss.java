package com.SirBlobman.notify;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.notify.config.ConfigNotifier;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class CustomBoss {
    private static Map<Player, BossBar> BOSS = Util.newMap();

    public static BossBar getBossBar(Player p) {
        if (BOSS.containsKey(p)) {
            BossBar bb = BOSS.get(p);
            return bb;
        } else {
            int def = ConfigOptions.OPTION_TIMER;
            List<String> l1 = Util.newList("{time_left}");
            List<Object> l2 = Util.newList(def);
            String title = Util.formatMessage(ConfigLang.MESSAGE_BOSS_BAR, l1, l2);
            BarStyle bs = BarStyle.SOLID;
            BarColor bc = null;
            String color = ConfigNotifier.BOSS_BAR_COLOR;
            try {
                bc = BarColor.valueOf(color);
            } catch (Throwable ex) {
                bc = null;
            } finally {
                if (bc == null) {
                    String error = "Invalid Boss Bar Color in config '" + color + "'. Defaulting to YELLOW";
                    Notifier.log(error);
                    bc = BarColor.YELLOW;
                }
            }
            BossBar bb = Bukkit.createBossBar(title, bc, bs);
            bb.setVisible(true);
            bb.addPlayer(p);
            BOSS.put(p, bb);
            return getBossBar(p);
        }
    }

    public static void changeTime(Player p, long time) {
        if (ConfigNotifier.USE_BOSS_BAR) {
            List<String> l1 = Util.newList("{time_left}");
            List<Object> l2 = Util.newList(time);
            String title = Util.formatMessage(ConfigLang.MESSAGE_BOSS_BAR, l1, l2);
            BossBar bb = getBossBar(p);
            double top = time;
            double bot = ConfigOptions.OPTION_TIMER;
            double div = (top / bot);
            bb.setProgress(div);
            bb.setTitle(title);
        }
    }

    public static void remove(Player p) {
        if (ConfigNotifier.USE_BOSS_BAR) {
            BossBar bb = getBossBar(p);
            String title = Util.color(ConfigLang.MESSAGE_EXPIRE);
            bb.setTitle(title);
            bb.setProgress(0);
            Util.runLater(new Runnable() {
                @Override
                public void run() {
                    bb.setVisible(false);
                    bb.removeAll();
                    BOSS.remove(p);
                }
            }, 40L);
        }
    }
}