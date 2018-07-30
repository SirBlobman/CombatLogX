package com.SirBlobman.combatlogx.utility;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.IntStream;

public class Util {
    public static final Plugin PLUGIN = CombatLogX.INSTANCE;
    public static final Server SERVER = Bukkit.getServer();
    public static final ConsoleCommandSender CONSOLE = SERVER.getConsoleSender();
    public static final PluginManager PM = SERVER.getPluginManager();
    public static final BukkitScheduler BS = SERVER.getScheduler();

    public static String color(String o) {
        return ChatColor.translateAlternateColorCodes('&', o);
    }

    public static String strip(String c) {
        return ChatColor.stripColor(c);
    }

    public static String[] color(String... ss) {
        IntStream.range(0, ss.length).forEach(i -> {
            String s = ss[i];
            String c = color(s);
            ss[i] = c;
        });
        return ss;
    }

    public static String[] strip(String... cc) {
        IntStream.range(0, cc.length).forEach(i -> {
            String c = cc[i];
            String s = strip(c);
            cc[i] = s;
        });
        return cc;
    }

    public static String str(Object o) {
        if (o == null)
            return "";
        else if ((o instanceof Short) || (o instanceof Integer) || (o instanceof Long)) {
            Number n = (Number) o;
            long l = n.longValue();
            return Long.toString(l);
        } else if ((o instanceof Float) || (o instanceof Double) || (o instanceof Number)) {
            Number n = (Number) o;
            double d = n.doubleValue();
            return Double.toString(d);
        } else if (o instanceof Location) {
            Location l = (Location) o;
            World w = l.getWorld();
            String sw = str(w);
            int x = l.getBlockX();
            int y = l.getBlockY();
            int z = l.getBlockZ();
            String s = "%1s: X: %2s, Y: %3s, Z: %4s";
            return format(s, sw, x, y, z);
        } else if (o instanceof Plugin) {
            Plugin pl = (Plugin) o;
            return pl.getName();
        } else if (o instanceof String) {
            return (String) o;
        } else {
            try {
                Class<?> clazz = o.getClass();
                Method m = clazz.getDeclaredMethod("getName");
                return (String) m.invoke(o);
            } catch (Throwable ex1) {
                try {
                    Class<?> clazz = o.getClass();
                    Method m = clazz.getDeclaredMethod("name");
                    return (String) m.invoke(o);
                } catch (Throwable ex2) {
                    return o.toString();
                }
            }
        }
    }

    public static String[] str(Object... oo) {
        String[] ss = new String[oo.length];

        IntStream.range(0, oo.length).forEach(i -> {
            Object o = oo[i];
            String s = str(o);
            ss[i] = s;
        });
        return ss;
    }

    public static String format(Object o, Object... oo) {
        String s = str(o);
        Object[] ss = str(oo);
        String f = String.format(s, ss);
        return color(f);
    }

    public static String formatMessage(Object o, List<String> keys, List<? extends Object> vals, Object... extra) {
        String s = str(o);
        int klen = keys.size(), vlen = vals.size();
        if (klen == vlen) {
            for (int i = 0; i < klen; i++) {
                String sk = keys.get(i);
                Object ov = vals.get(i);
                String sv = str(ov);
                s = s.replace(sk, sv);
            }

            return format(s, extra);
        } else {
            String error = "You must have the same amount of keys as you have values!";
            throw new IllegalArgumentException(error);
        }
    }

    public static void print(Object... oo) {
        Arrays.stream(oo).map(object -> color(ConfigLang.MESSAGE_PREFIX + str(object))).forEach(prefix -> {
            if (prefix.equals("\n"))
                prefix = color("&l");
            CONSOLE.sendMessage(prefix);
        });
    }

    public static void printNoPrefix(Object... oo) {
        Arrays.stream(oo).map(object -> color(str(object))).forEach(Util::accept);
    }

    public static void broadcast(Object... oo) {
        print(oo);

        Bukkit.getOnlinePlayers().forEach(player -> Arrays.stream(oo).forEach(object -> {
            String color = color(ConfigLang.MESSAGE_PREFIX + str(object));

            player.sendMessage(color);
        }));
    }

    public static void sendMessage(CommandSender cs, Object... oo) {
        if (cs instanceof Entity) {
            Entity en = (Entity) cs;
            World world = en.getWorld();
            String name = world.getName().toLowerCase();
            List<String> disabled = toLowerCaseList(ConfigOptions.OPTION_DISABLED_WORLDS);

            if (disabled.contains(name))
                return;
        }

        Arrays.stream(oo).filter(object -> !str(object).isEmpty() && !str(object).equals("")).map(object -> color(ConfigLang.MESSAGE_PREFIX + str(object))).forEach(cs::sendMessage);
    }
    
    public static void sendInfoMessage(CommandSender cs, Object... oo) {
        Arrays.stream(oo).filter(object -> str(object) != null && !str(object).isEmpty() && !str(object).equals("")).map(object -> color(str(object))).forEach(cs::sendMessage);
    }

    public static void regEvents(Listener... ll) {
        regEvents(PLUGIN, ll);
    }

    public static void regEvents(Plugin p, Listener... ll) {
        Arrays.stream(ll).filter(Objects::nonNull).forEach(listener -> PM.registerEvents(listener, p));
    }

    public static void call(Event... ee) {
        Arrays.stream(ee).filter(Objects::nonNull).forEach(PM::callEvent);
    }

    public static BukkitTask runLater(Runnable r, long delay) {
        return BS.runTaskLater(PLUGIN, r, delay);
    }

    public static BukkitTask runTimer(Runnable r, long timer, long delay) {
        return BS.runTaskTimer(PLUGIN, r, delay, timer);
    }

    @SafeVarargs
    public static <S> Set<S> newSet(S... ss) {
        return new HashSet<>(Arrays.asList(ss));
    }

    @SafeVarargs
    public static <L> List<L> newList(L... ll) {
        return new ArrayList<>(Arrays.asList(ll));
    }

    public static <L> List<L> newList(Collection<L> ll) {
        return new ArrayList<>(ll);
    }

    public static List<String> toLowerCaseList(List<String> originalList) {
        List<String> lower = newList();

        originalList.forEach(caps -> lower.add(caps.toLowerCase()));

        return lower;
    }

    public static <K, V> HashMap<K, V> newMap() {
        return new HashMap<>();
    }

    private static void accept(String color) {
        if (color.equals("\n"))
            color = color("&l");
        CONSOLE.sendMessage(color);
    }
}