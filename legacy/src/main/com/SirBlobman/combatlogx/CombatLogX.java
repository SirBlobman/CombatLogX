package com.SirBlobman.combatlogx;

import com.SirBlobman.combatlogx.command.CommandCombatLogX;
import com.SirBlobman.combatlogx.command.CommandCombatTime;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.listener.FinalMonitor;
import com.SirBlobman.combatlogx.listener.ListenBukkit;
import com.SirBlobman.combatlogx.nms.NMSUtil;
import com.SirBlobman.combatlogx.utility.UpdateUtil;
import com.SirBlobman.combatlogx.utility.Util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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
            
            command("combatlogx", new CommandCombatLogX());
            command("combattime", new CommandCombatTime());
            
            int majorMCVersion = NMSUtil.getMajorVersion();
            int minorMCVersion = NMSUtil.getMinorVersion();
            if (majorMCVersion > 1 || minorMCVersion > 12) {
                Util.print("This version of CombatLogX is meant for 1.8-1.12.2");
                Util.print("1.13+ IS NOT SUPPORTED");
                Util.print("Disabling CombatLogX....");
                
                try {
                    PluginManager plm = Bukkit.getPluginManager();
                    Class<?> class_PluginManager = plm.getClass();
                    Field field_commandMap = class_PluginManager.getDeclaredField("commandMap");
                    field_commandMap.setAccessible(true);
                    
                    Object commandMap = field_commandMap.get(plm);
                    Class<?> class_CommandMap = commandMap.getClass().getSuperclass();
                    Field field_knownCommands = class_CommandMap.getDeclaredField("knownCommands");
                    field_knownCommands.setAccessible(true);
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Command> knownCommands = (Map<String, Command>) field_knownCommands.get(commandMap);
                    
                    Util.newMap(knownCommands).forEach((label, command) -> {
                        if(command instanceof PluginCommand) {
                            PluginCommand pc = (PluginCommand) command;
                            Plugin pl = pc.getPlugin();
                            if(pl instanceof CombatLogX) knownCommands.remove(label);
                        }
                    });
                    
                    field_knownCommands.setAccessible(false);
                    field_commandMap.setAccessible(false);
                } catch(Throwable ex) {
                    ex.printStackTrace();
                }
                
                Bukkit.getPluginManager().disablePlugin(INSTANCE);
                return;
            } else {                
                if (ConfigOptions.OPTION_CHECK_UPDATES) UpdateUtil.checkForUpdates();
                Util.regEvents(new ListenBukkit(), new FinalMonitor());
                Util.runTimer(new Combat(), 20, 0);
                Expansions.loadExpansions();
                if (ConfigOptions.OPTION_BROADCAST_STARTUP) Util.broadcast("&2Enabled");
            }
        }, 0);
    }
    
    @Override
    public void onDisable() {
        Expansions.onDisable();
    }
    
    public void command(String cmd, CommandExecutor ce) {
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
                String error = Util.format("The command '%s' cannot have a NULL executor", cmd);
                Util.print(error);
            }
        } else {
            String error = Util.format("The command '%s' is not inside of the 'plugin.yml' of CombatLogX", cmd);
            Util.print(error);
        }
    }
}