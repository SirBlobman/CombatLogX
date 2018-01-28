package com.SirBlobman.combatlogx.utility;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.config.ConfigLang;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Util {
    public static final Plugin PLUGIN = CombatLogX.INSTANCE;
    public static final Server SERVER = Bukkit.getServer();
    public static final ConsoleCommandSender CONSOLE = SERVER.getConsoleSender();
    public static final PluginManager PM = SERVER.getPluginManager();
    public static final BukkitScheduler BS = SERVER.getScheduler();

    public static String color(String o) {return ChatColor.translateAlternateColorCodes('&', o);}
    public static String strip(String c) {return ChatColor.stripColor(c);}

    public static String[] color(String... ss) {
        for(int i = 0; i < ss.length; i++) {
            String s = ss[i];
            String c = color(s);
            ss[i] = c;
        } return ss;
    }

    public static String[] strip(String... cc) {
        for(int i = 0; i < cc.length; i++) {
            String c = cc[i];
            String s = strip(c);
            cc[i] = s;
        } return cc;
    }

    public static String str(Object o) {
        if(o == null) return "";
        else if((o instanceof Short) || (o instanceof Integer) || (o instanceof Long)) {
            Number n = (Number) o;
            long l = n.longValue();
            String s = Long.toString(l);
            return s;
        } else if((o instanceof Float) || (o instanceof Double) || (o instanceof Number)) {
            Number n = (Number) o;
            double d = n.doubleValue();
            String s = Double.toString(d);
            return s;
        } else if(o instanceof Location) {
            Location l = (Location) o;
            World w = l.getWorld();
            String sw = str(w);
            int x = l.getBlockX();
            int y = l.getBlockY();
            int z = l.getBlockZ();
            String s = "%1s: X: %2s, Y: %3s, Z: %4s";
            String f = format(s, sw, x, y, z);
            return f;
        } else if(o instanceof Plugin) {
            Plugin pl = (Plugin) o;
            String name = pl.getName();
            return name;
        } else if(o instanceof String) {
            String s = (String) o;
            return s;
        } else {
            try {
                Class<?> clazz = o.getClass();
                Method m = clazz.getDeclaredMethod("getName");
                String s = (String) m.invoke(o);
                return s;
            } catch(Throwable ex1) {
                try {
                    Class<?> clazz = o.getClass();
                    Method m = clazz.getDeclaredMethod("name");
                    String s = (String) m.invoke(o);
                    return s;
                } catch(Throwable ex2) {
                    String s = o.toString();
                    return s;
                }
            }
        }
    }

    public static String[] str(Object... oo) {
        String[] ss = new String[oo.length];
        for(int i = 0; i < oo.length; i++) {
            Object o = oo[i];
            String s = str(o);
            ss[i] = s;
        } return ss;
    }

    public static String format(Object o, Object... oo) {
        String s = str(o);
        Object[] ss = str(oo);
        String f = String.format(s, ss);
        String c = color(f);
        return c;
    }

    public static String formatMessage(Object o, List<String> keys, List<? extends Object> vals, Object... extra) {
        String s = str(o);
        int klen = keys.size(), vlen = vals.size();
        if(klen == vlen) {
            for(int i = 0; i < klen; i++) {
                String sk = keys.get(i);
                Object ov = vals.get(i);
                String sv = str(ov);
                s = s.replace(sk, sv);
            }

            String f = format(s, extra);
            return f;
        } else {
            String error = "You must have the same amount of keys as you have values!";
            IllegalArgumentException iae = new IllegalArgumentException(error);
            throw iae;
        }
    }

    public static void print(Object... oo) {
        for(Object o : oo) {
            String s = str(o);
            String c = color(ConfigLang.MESSAGE_PREFIX + s);
            if(s.equals("\n")) c = color("&l");
            CONSOLE.sendMessage(c);
        }
    }

    public static void broadcast(Object... oo) {
        print(oo);
        Collection<? extends Player> cp = SERVER.getOnlinePlayers();
        for(Player p : cp) {
            for(Object o : oo) {
                String s = str(o);
                String c = color(ConfigLang.MESSAGE_PREFIX + s);
                p.sendMessage(c);
            }
        }
    }
    
    public static void sendMessage(CommandSender cs, Object... oo) {
        for(Object o : oo) {
            String s = str(o);
            if(s.isEmpty() || s.equals("")) continue;
            else {
                String c = color(ConfigLang.MESSAGE_PREFIX + s);
                cs.sendMessage(c);
            }
        }
    }


    public static void regEvents(Listener... ll) {regEvents(PLUGIN, ll);}
    public static void regEvents(Plugin p, Listener... ll) {
        for(Listener l : ll) {
            if(l != null) PM.registerEvents(l, p);
        }
    }
    
    public static void call(Event... ee) {
        for(Event e : ee) {
            if(e != null) PM.callEvent(e);
        }
    }
    
    public static BukkitTask runLater(Runnable r, long delay) {
        BukkitTask bt = BS.runTaskLater(PLUGIN, r, delay);
        return bt;
    }
    
    public static BukkitTask runTimer(Runnable r, long timer, long delay) {
        BukkitTask bt = BS.runTaskTimer(PLUGIN, r, delay, timer);
        return bt;
    }

    @SafeVarargs
    public static <L> List<L> newList(L... ll) {
        List<L> list = new ArrayList<L>();
        for(L l : ll) list.add(l);
        return list;
    }
    
    public static <L> List<L> newList(Collection<L> ll) {
        List<L> list = new ArrayList<L>();
        for(L l : ll) list.add(l);
        return list;
    }

    public static <K, V> HashMap<K, V> newMap() {
        HashMap<K, V> map = new HashMap<K, V>();
        return map;
    }
}