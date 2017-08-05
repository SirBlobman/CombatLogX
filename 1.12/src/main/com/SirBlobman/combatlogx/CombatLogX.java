package com.SirBlobman.combatlogx;

import com.SirBlobman.combatlogx.command.CommandCombatTime;
import com.SirBlobman.combatlogx.command.CommandConfig;
import com.SirBlobman.combatlogx.config.Config;
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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class CombatLogX extends JavaPlugin {
    private static final String SPIGOT_KEY = "98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4";
    private static final String SPIGOT_ID = "31689";

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
                Config.loadC(); Config.loadL();
                if(Config.OPTION_CHECK_UPDATES) checkUpdate();
                command("combattime", new CommandCombatTime());
                command("clxconfig", new CommandConfig());
                Util.regEvents(new ListenBukkit(), new FinalMonitor());
                Util.runTimer(new Combat(), 20, 0);
                Expansions.loadExpansions();
                Util.broadcast("&2Enabled");
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
            String site = "http://www.spigotmc.org/api/general.php";
            URL url = new URL(site);
            URLConnection urlc = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) urlc;
            http.setDoOutput(true);
            http.setRequestMethod("POST");

            OutputStream os = http.getOutputStream();
            String req = "key=" + SPIGOT_KEY + "&resource=" + SPIGOT_ID;
            byte[] write = req.getBytes("UTF-8");
            os.write(write);

            InputStream is = http.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String version = br.readLine();
            return version;
        } catch(Throwable ex) {
            String error = "Failed to check for updates:";
            Util.print(error);
            ex.printStackTrace();
            return pluginVersion();
        }
    }
    
    private static String pluginVersion() {
        PluginDescriptionFile pdf = INSTANCE.getDescription();
        String version = pdf.getVersion();
        return version;
    }
    
    private static void checkUpdate() {
        String spigot = spigotVersion();
        String plugin = pluginVersion();
        if(spigot.equals(plugin)) {
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
    
    private static void print(String... ss) {
        for(String s : ss) {
            String c = Util.color(s);
            ConsoleCommandSender ccs = Util.CONSOLE;
            ccs.sendMessage(c);
        }
    }
}