package com.SirBlobman.notify;

import java.io.File;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.CombatTimerChangeEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.nms.NMSUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.combatlogx.utility.WordUtil;
import com.SirBlobman.notify.config.ConfigNotifier;

public class Notifier implements CLXExpansion, Listener {
    private static List<String> VALID_BOSS = Util.newList("1.12", "1.11", "1.10");
    private static NMSUtil NMS;
    public static Notifier INSTANCE;
    public static File FOLDER;
    
    @Override
    public void enable() {
        INSTANCE = this;
        FOLDER = getDataFolder();
        NMS = NMSUtil.getNMS();
        onConfigReload();
        
        Util.regEvents(this);
    }
    
    public String getUnlocalizedName() {
        return "Notifier";
    }
    
    public String getName() {
        return getUnlocalizedName();
    }
    
    public String getVersion() {
        return "3";
    }
    
    @Override
    public void onConfigReload() {
        ConfigNotifier.load();
        
        String base = NMSUtil.baseVersion();
        if (NMS == null) ConfigNotifier.USE_ACTION_BAR = false;
        if (!VALID_BOSS.contains(base)) {
            ConfigNotifier.USE_BOSS_BAR = false;
            String error = base + " does not support boss bars";
            print(error);
        }
    }
    
    @EventHandler
    public void pue(PlayerUntagEvent e) {
        Player p = e.getPlayer();
        if (ConfigNotifier.USE_BOSS_BAR) CustomBoss.remove(p);
        if (ConfigNotifier.USE_SCOREBOARD) CustomScore.remove(p);
        if (ConfigNotifier.USE_ACTION_BAR) NMS.action(p, "");
    }
    
    @EventHandler
    public void ctce(CombatTimerChangeEvent e) {
        Player p = e.getPlayer();
        int time = (int) e.secondsLeft();
        if (ConfigNotifier.USE_BOSS_BAR) CustomBoss.changeTime(p, time);
        if (ConfigNotifier.USE_SCOREBOARD) CustomScore.update(p);
        if (ConfigNotifier.USE_ACTION_BAR) {
            int bars_right = (ConfigOptions.OPTION_TIMER - time);
            int bars_left = (ConfigOptions.OPTION_TIMER - bars_right);
            List<String> l1 = Util.newList("{time_left}", "{bars_left}", "{bars_right}");
            List<Object> l2 = Util.newList(time, WordUtil.getTimeBars(bars_left), WordUtil.getPassedBars(bars_right));
            String msg = Util.formatMessage(ConfigLang.MESSAGE_ACTION_BAR, l1, l2);
            NMS.action(p, msg);
        }
    }
    
    public static void log(Object... oo) {
        INSTANCE.print(oo);
    }
}