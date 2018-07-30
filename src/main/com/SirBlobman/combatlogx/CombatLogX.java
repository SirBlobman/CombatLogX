package com.SirBlobman.combatlogx;

import com.SirBlobman.combatlogx.command.CommandCombatLogX;
import com.SirBlobman.combatlogx.command.CommandCombatTime;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.listener.FinalMonitor;
import com.SirBlobman.combatlogx.listener.ListenBukkit;
import com.SirBlobman.combatlogx.utility.UpdateUtil;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CombatLogX extends JavaPlugin {
    public static CombatLogX INSTANCE;
    public static File FOLDER;
    public static ClassLoader CLASS_LOADER;

    @Override
    public void onEnable() {
        INSTANCE = this;
        FOLDER = getDataFolder();
        CLASS_LOADER = getClassLoader();
        Util.runLater(() -> {
            ConfigOptions.load();
            ConfigLang.load();
            if (ConfigOptions.OPTION_CHECK_UPDATES)
                UpdateUtil.checkForUpdates();
            command("combatlogx", new CommandCombatLogX());
            command("combattime", new CommandCombatTime());
            Util.regEvents(new ListenBukkit(), new FinalMonitor());
            Util.runTimer(new Combat(), 20, 0);
            Expansions.loadExpansions();
            if (ConfigOptions.OPTION_BROADCAST_STARTUP) Util.broadcast("&2Enabled");
        }, 0);
    }

    @Override
    public void onDisable() {
        Expansions.onDisable();
    }

    private void command(String cmd, CommandExecutor ce) {
        PluginCommand pc = getCommand(cmd);
        if (pc != null) {
            if (ce != null) {
                pc.setExecutor(ce);
                if (ce instanceof TabCompleter) {
                    TabCompleter tc = (TabCompleter) ce;
                    pc.setTabCompleter(tc);
                }

                if (ce instanceof Listener) {
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
}