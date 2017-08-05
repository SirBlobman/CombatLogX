package com.SirBlobman.combatlogx;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.event.CombatTimerChangeEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagCause;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Nullable;

public class Combat implements Runnable {
    private static Map<Player, Long> COMBAT = Util.newMap();
    private static Map<Player, LivingEntity> ENEMIES = Util.newMap();
    
    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        HashMap<Player, Long> combat = (HashMap<Player, Long>) COMBAT;
        HashMap<Player, Long> clone = (HashMap<Player, Long>) combat.clone();
        Set<Player> set = clone.keySet();
        for(Player p : set) {
            long seconds = timeLeft(p);
            if(seconds <= 0) {
                PlayerUntagEvent pue = new PlayerUntagEvent(p, UntagCause.EXPIRE);
                Util.call(pue);
            } else {
                CombatTimerChangeEvent ctce = new CombatTimerChangeEvent(p, seconds);
                Util.call(ctce);
            }
        }
    }
    
    public static boolean isInCombat(Player p) {
        if(COMBAT.containsKey(p)) return true;
        else return false;
    }
    
    public static long timeLeft(Player p) {
        if(isInCombat(p)) {
            long millis = COMBAT.get(p);
            long system = System.currentTimeMillis();
            long mileft = (millis - system);
            long seconds = (mileft / 1000);
            return seconds;
        } else return -1;
    }
    
    public static LivingEntity getEnemy(Player p) {
        if(isInCombat(p)) {
            LivingEntity le = ENEMIES.get(p);
            return le;
        } else return null;
    }
    
    public static Player getByEnemy(LivingEntity le) {
        List<LivingEntity> list = enemyList();
        if(list.contains(le)) {
            for(Entry<Player, LivingEntity> e : ENEMIES.entrySet()) {
                LivingEntity check = e.getValue();
                if(le.equals(check)) {
                    Player p = e.getKey();
                    return p;
                }
            } return null;
        } else return null;
    }
    
    public static List<LivingEntity> enemyList() {
        Collection<LivingEntity> lee = ENEMIES.values();
        List<LivingEntity> list = Util.newList(lee);
        return list;
    }
    
    /**
     * Add a player into the combat timer
     * be sure to check if they have bypass
     * @param p Player to add
     * @param enemy The entity that cause them to be put into combat (can be {@code null})
     * @see {@link com.SirBlobman.combatlogx.utility.CombatUtil#canBeTagged(Player)}
     */
    public static void tag(Player p, @Nullable LivingEntity enemy) {
        long current = System.currentTimeMillis();
        long timer = (Config.OPTION_TIMER * 1000L);
        long time = (current + timer);
        COMBAT.put(p, time);
        ENEMIES.put(p, enemy);
        
        if(!isInCombat(p)) {
            if(Config.OPTION_COMBAT_SUDO_ENABLE) {
                List<String> list = Config.OPTION_COMBAT_SUDO_COMMANDS;
                for(String cmd : list) p.performCommand(cmd);
            }
        }
    }
    
    public static void remove(Player p) {
        COMBAT.remove(p);
        ENEMIES.remove(p);
    }

    public static void punish(Player p) {
        if(Config.PUNISH_KILL_PLAYER) p.setHealth(0.0D);

        if(Config.PUNISH_ON_QUIT_MESSAGE) {
            List<String> l1 = Util.newList("{player}");
            List<String> l2 = Util.newList(p.getName());
            String msg = Util.formatMessage(Config.MESSAGE_QUIT, l1, l2);
            Util.broadcast(msg);
        }

        if(Config.PUNISH_SUDO_LOGGERS) {
            List<String> list = Config.PUNISH_COMMANDS_LOGGERS;
            for(String cmd : list) {
                cmd = format(p, cmd);
                p.performCommand(cmd);
            }
        }

        if(Config.PUNISH_CONSOLE) {
            List<String> list = Config.PUNISH_COMMANDS_CONSOLE;
            for(String cmd : list) {
                cmd = format(p, cmd);
                Bukkit.dispatchCommand(Util.CONSOLE, cmd);
            }
        }
    }
    
    private static String format(Player p, String cmd) {
        String name = p.getName();
        List<String> l1 = Util.newList("{player}");
        List<?> l2 = Util.newList(name);
        String f = Util.formatMessage(cmd, l1, l2);
        return f;
    }
}