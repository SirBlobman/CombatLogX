package com.SirBlobman.combatlogx;

import com.SirBlobman.combatlogx.command.CommandCombatTime;
import com.SirBlobman.combatlogx.command.CommandConfig;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.listener.FinalMonitor;
import com.SirBlobman.combatlogx.listener.ListenBukkit;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class CombatLogX extends JavaPlugin {
    private static final String SPIGOT_URL = "https://api.spigotmc.org/legacy/update.php?resource=31689";

    public static CombatLogX INSTANCE;
    public static File FOLDER;
    public static ClassLoader CLASS_LOADER;

    @Override
    public void onEnable() {
        INSTANCE = this;
        FOLDER = getDataFolder();
        CLASS_LOADER = getClassLoader();
        Util.runLater(new Runnable() {
            @Override
            public void run() {
                ConfigOptions.load();
                ConfigLang.load();
                if(ConfigOptions.OPTION_CHECK_UPDATES) checkUpdate();
                command("combattime", new CommandCombatTime());
                command("clxconfig", new CommandConfig());
                Util.regEvents(new ListenBukkit(), new FinalMonitor());
                Util.runTimer(new Combat(), 20, 0);
                Expansions.loadExpansions();
                if(ConfigOptions.OPTION_BROADCAST_STARTUP) Util.broadcast("&2Enabled");
            }
        }, 0);
    }
    
    @Override
    public void onDisable() {
        Expansions.onDisable();
    }

    public void command(String cmd, CommandExecutor ce) {
        PluginCommand pc = getCommand(cmd);
        if(pc != null) {
            if(ce != null) {
                pc.setExecutor(ce);
                if(ce instanceof TabCompleter) {
                    TabCompleter tc = (TabCompleter) ce;
                    pc.setTabCompleter(tc);
                }

                if(ce instanceof Listener) {
                    Listener l = (Listener) ce;
                    Util.regEvents(l);
                }
            } else {
                String error = Util.format("The command '%1s' cannot have a NULL executor", cmd);
                Util.print(error);
            }
        } else {
            String error = Util.format("The command '%1s' is not inside of the 'plugin.yml' of CombatLogX", cmd);
            Util.print(error);
        }
    }

    private static String spigotVersion() {
        try {
            URL url = new URL(SPIGOT_URL);
            URLConnection urlc = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) urlc;
            http.setRequestMethod("GET");
            
            InputStream is = http.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String version = br.readLine();
            br.close(); isr.close(); is.close();
            return version;
        } catch(Throwable ex) {return pluginVersion();}
    }
    
    private static String pluginVersion() {
        PluginDescriptionFile pdf = INSTANCE.getDescription();
        String version = pdf.getVersion();
        return version;
    }
    
    private static void checkUpdate() {
        Util.BS.runTaskAsynchronously(INSTANCE, new Runnable() {
           @Override
           public void run() {
               String spigot = spigotVersion();
               String plugin = pluginVersion();
               if(spigot == null || plugin == null) {
                   String[] error = Util.color(
                       "&6========================================================",
                       "&eCombatLogX Update Checker",
                       "&cCould not connect to Spigot's API",
                       "&6========================================================"
                   );
                   print(error);
               } else if(plugin.equals(spigot)) {
                   String[] msg = Util.color(
                       "&6========================================================",
                       "&eCombatLogX Update Checker",
                       "&aYou are using the latest version!",
                       "&6========================================================"
                   );
                   print(msg);
               } else {
                   String[] msg = Util.color(
                       "&6========================================================",
                       "&eCombatLogX Update Checker",
                       "&aThere is a new update available!",
                       "&e&lLatest Version: &a" + spigot,
                       "&e&lYour Version: &c" + plugin,
                       "&eGet it here: &b&nhttps://www.spigotmc.org/resources/combatlogx.31689/",
                       "&6========================================================"
                   );
                   print(msg);
               }
           }
        });
    }
    
    private static void print(String... ss) {
        for(String s : ss) {
            String c = Util.color(s);
            ConsoleCommandSender ccs = Util.CONSOLE;
            ccs.sendMessage(c);
        }
    }
}