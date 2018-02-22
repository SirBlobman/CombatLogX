package com.SirBlobman.notify;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.CombatTimerChangeEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.combatlogx.utility.WordUtil;
import com.SirBlobman.notify.nms.NMSUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class Notifier implements CLXExpansion, Listener {
    private static List<String> VALID_BOSS = Util.newList("1.12", "1.11", "1.10");
    private static NMSUtil NMS;
    private static Notifier INSTANCE;

    @Override
    public void enable() {
        INSTANCE = this;
        NMS = NMSUtil.getNMS();
        if(NMS == null) ConfigOptions.OPTION_ACTION_BAR = false;
        String base = NMSUtil.baseVersion();
        if(!VALID_BOSS.contains(base)) {
            ConfigOptions.OPTION_BOSS_BAR = false;
            String error = base + " does not support boss bars";
            print(error);
        }
        
        Util.regEvents(this);
    }

    public String getUnlocalizedName() {return getName();}
    public String getName() {return "Notifier";}
    public String getVersion() {return "1.0.3";}

    @EventHandler
    public void pue(PlayerUntagEvent e) {
        Player p = e.getPlayer();
        if(ConfigOptions.OPTION_BOSS_BAR) CustomBoss.remove(p);
        if(ConfigOptions.OPTION_SCORE_BOARD) CustomScore.remove(p);
        if(ConfigOptions.OPTION_ACTION_BAR) NMS.action(p, "");
    }

    @EventHandler
    public void ctce(CombatTimerChangeEvent e) {
        Player p = e.getPlayer();
        int time = (int) e.secondsLeft();
        if(ConfigOptions.OPTION_BOSS_BAR) CustomBoss.changeTime(p, time);
        if(ConfigOptions.OPTION_SCORE_BOARD) CustomScore.update(p);
        if(ConfigOptions.OPTION_ACTION_BAR) {
            int bars_right = (ConfigOptions.OPTION_TIMER - time);
            int bars_left = (ConfigOptions.OPTION_TIMER - bars_right);
            List<String> l1 = Util.newList("{time_left}", "{bars_left}", "{bars_right}");
            List<Object> l2 = Util.newList(time, WordUtil.getTimeBars(bars_left), WordUtil.getPassedBars(bars_right));
            String msg = Util.formatMessage(ConfigLang.MESSAGE_ACTION_BAR, l1, l2);
            NMS.action(p, msg);
        }
    }
    
    public static void log(Object... oo) {INSTANCE.print(oo);}
}