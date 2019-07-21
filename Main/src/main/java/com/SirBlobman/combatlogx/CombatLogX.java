package com.SirBlobman.combatlogx;

import java.io.File;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.combatlogx.command.CommandCombatLogX;
import com.SirBlobman.combatlogx.command.CommandCombatTime;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.listener.AttackListener;
import com.SirBlobman.combatlogx.listener.CombatListener;
import com.SirBlobman.combatlogx.listener.FinalMonitor;
import com.SirBlobman.combatlogx.listener.PunishListener;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.UpdateUtil;
import com.SirBlobman.combatlogx.utility.Util;

public class CombatLogX extends JavaPlugin {
    public static CombatLogX INSTANCE;
    public static File FOLDER;
    public static ClassLoader CLASS_LOADER;

    @Override
    public void onLoad() {
        INSTANCE = this;
        FOLDER = getDataFolder();
        CLASS_LOADER = getClassLoader();
        
        ConfigOptions.load();
        ConfigLang.load();

        String loadMSG = ConfigLang.get("messages.broadcasts.on load");
        Util.broadcast(true, loadMSG);
        Expansions.loadExpansions();
    }
    
    @Override
    public void onEnable() {
        SchedulerUtil.runLater(0L, () -> {
            if (ConfigOptions.OPTION_CHECK_FOR_UPDATES) UpdateUtil.checkForUpdates();

            registerCommand("combatlogx", CommandCombatLogX.class);
            registerCommand("combattime", CommandCombatTime.class);

            SchedulerUtil.runTimer(0L, 10L, new CombatUtil());

            PluginUtil.regEvents(new FinalMonitor(), new CombatListener(), new PunishListener(), new AttackListener());

            Expansions.enableExpansions();

            if (ConfigOptions.OPTION_BROADCAST_ENABLE_MESSAGE) {
                String broadcast = ConfigLang.get("messages.broadcasts.on enable");
                Util.broadcast(true, broadcast);
            }

        });
    }

    @Override
    public void onDisable() {
        CombatUtil.getPlayersInCombat().forEach(player -> CombatUtil.untag(player, UntagReason.EXPIRE));

        Expansions.onDisable();

        if (ConfigOptions.OPTION_BROADCAST_DISABLE_MESSAGE) {
            String broadcast = ConfigLang.get("messages.broadcasts.on disable");
            Util.broadcast(true, broadcast);
        }
    }

    private void registerCommand(String name, Class<? extends CommandExecutor> clazz) {
        try {
            CommandExecutor ce = clazz.getDeclaredConstructor().newInstance();
            PluginCommand pc = getCommand(name);
            if (pc == null) {
                String error = "An error has occured. If you do not understand this, send the following error code to SirBlobman:\nCLX-cmd-01";
                Util.print(error);
            } else {
                if (ce == null) {
                    String error = "An error has occured. If you do not understand this, send the following error code to SirBlobman:\nCLX-cmd-02";
                    Util.print(error);
                } else {
                    pc.setExecutor(ce);

                    if (ce instanceof TabCompleter) {
                        TabCompleter tc = (TabCompleter) ce;
                        pc.setTabCompleter(tc);
                    }

                    if (ce instanceof Listener) {
                        Listener l = (Listener) ce;
                        PluginUtil.regEvents(l);
                    }
                }
            }
        } catch (Throwable ex) {
            String error = "An error has occured. If you do not understand this, send the following error code to SirBlobman:\nCLX-cmd-00";
            Util.print(error);
        }
    }
}