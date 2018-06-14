package com.SirBlobman.combatlogx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.CombatTimerChangeEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagCause;
import com.SirBlobman.combatlogx.utility.OldUtil;
import com.SirBlobman.combatlogx.utility.Util;

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
        World w = p.getWorld();
        String world = w.getName().toLowerCase();
        List<String> disabled = Util.toLowerCaseList(ConfigOptions.OPTION_DISABLED_WORLDS);
        if(!disabled.contains(world)) {
            long current = System.currentTimeMillis();
            long timer = (ConfigOptions.OPTION_TIMER * 1000L);
            long time = (current + timer);
            
            if(!isInCombat(p)) {
                if(ConfigOptions.OPTION_COMBAT_SUDO_ENABLE) {
                    for(String cmd : ConfigOptions.OPTION_COMBAT_SUDO_COMMANDS) p.performCommand(cmd);
                    for(String cmd : ConfigOptions.OPTION_COMBAT_CONSOLE_COMMANDS) Bukkit.dispatchCommand(Util.CONSOLE, Util.formatMessage(cmd, Util.newList("{player}"), Util.newList(p.getName())));
                }
            }
            
            COMBAT.put(p, time);
            ENEMIES.put(p, enemy);
            CombatTimerChangeEvent ctce = new CombatTimerChangeEvent(p, ConfigOptions.OPTION_TIMER);
            Util.call(ctce);
        } else remove(p);
    }
    
    public static void remove(Player p) {
        COMBAT.remove(p);
        ENEMIES.remove(p);
    }

    public static void punish(Player p) {
        World w = p.getWorld();
        String world = w.getName().toLowerCase();
        List<String> disabled = Util.toLowerCaseList(ConfigOptions.OPTION_DISABLED_WORLDS);
        if(!disabled.contains(world)) {
            if(ConfigOptions.PUNISH_KILL_PLAYER) {
                PlayerInventory pi = p.getInventory();
                List<ItemStack> items = Util.newList(pi.getContents());
                String deathMessage = "";
                if(ConfigOptions.PUNISH_ON_QUIT_MESSAGE) {
                    List<String> l1 = Util.newList("{player}");
                    List<String> l2 = Util.newList(p.getName());
                    deathMessage = Util.formatMessage(ConfigLang.MESSAGE_QUIT, l1, l2);
                }

                p.setHealth(0);
            	PlayerDeathEvent pde = new PlayerDeathEvent(p, items, 1, deathMessage);
            	Util.call(pde);
            }

            if(ConfigOptions.PUNISH_SUDO_LOGGERS) {
                List<String> list = ConfigOptions.PUNISH_COMMANDS_LOGGERS;
                for(String cmd : list) {
                    cmd = format(p, cmd);
                    p.performCommand(cmd);
                }
            }

            if(ConfigOptions.PUNISH_CONSOLE) {
                List<String> list = ConfigOptions.PUNISH_COMMANDS_CONSOLE;
                for(String cmd : list) {
                    cmd = format(p, cmd);
                    Bukkit.dispatchCommand(Util.CONSOLE, cmd);
                }
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
    
    public static String log(LivingEntity attacker, LivingEntity target) {
        try {
            File folder = CombatLogX.FOLDER;
            File file = new File(folder, "combat.log");
            if(!file.exists()) {
                folder.mkdirs();
                file.createNewFile();
            }
            
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter log = new PrintWriter(bw);
            
            DateFormat df = new SimpleDateFormat("MMMM dd, yyyy | HH:mm:ss.SSS");
            Date now = Calendar.getInstance().getTime();
            String date = "[" + df.format(now) + "] ";
            
            if(attacker == null) {
                String msg = date + Util.formatMessage(ConfigLang.MESSAGE_LOG_TARGET_ONLY, Util.newList("{target}"), Util.newList(OldUtil.getName(target)));
                log.println(msg);
                log.close();
                return msg;
            }
            
            if(target == null) {
                String msg = date + Util.formatMessage(ConfigLang.MESSAGE_LOG_ATTACKER_ONLY, Util.newList("{attacker}"), Util.newList(OldUtil.getName(attacker)));
                log.println(msg);
                log.close();
                return msg;
            }
            
            String msg = date + Util.formatMessage(ConfigLang.MESSAGE_LOG_COMBAT, Util.newList("{attacker}", "{target}"), Util.newList(OldUtil.getName(attacker), OldUtil.getName(target)));
            log.println(msg);
            log.close();
            return msg;
        } catch(Throwable ex) {
            String error = "Failed to write to log file 'combat.log':";
            Util.print(error);
            ex.printStackTrace();
            return error;
        }
    }
}