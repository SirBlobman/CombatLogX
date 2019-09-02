package com.SirBlobman.combatlogx;

import com.SirBlobman.combatlogx.command.CommandCombatLogX;
import com.SirBlobman.combatlogx.command.CommandCombatTime;
import com.SirBlobman.combatlogx.command.CustomCommand;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.listener.AttackListener;
import com.SirBlobman.combatlogx.listener.CombatListener;
import com.SirBlobman.combatlogx.listener.FinalMonitor;
import com.SirBlobman.combatlogx.listener.PunishListener;
import com.SirBlobman.combatlogx.utility.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;

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
            if (ConfigOptions.OPTION_CHECK_FOR_UPDATES) UpdateUtil.checkForUpdates(this);

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
    		PluginCommand command = getCommand(name);
    		if(command == null) {
    			Util.print("Could not find the command '" + name + "' in plugin.yml, attempting to force register...");
    			forceRegisterCommand(name, clazz);
    			return;
    		}

			Constructor<? extends CommandExecutor> constructor;
    		CommandExecutor executor;
    		try {
    			 constructor = clazz.getDeclaredConstructor();
    			 executor = constructor.newInstance();
			} catch(ReflectiveOperationException ex) {
    			constructor = clazz.getDeclaredConstructor(CombatLogX.class);
    			executor = constructor.newInstance(this);
			}
    		command.setExecutor(executor);

    		if(executor instanceof TabCompleter) {
    			TabCompleter completer = (TabCompleter) executor;
    			command.setTabCompleter(completer);
    		}
    		
    		if(executor instanceof Listener) {
    			Listener listener = (Listener) executor;
    			PluginUtil.regEvents(listener);
    		}
    	} catch(ReflectiveOperationException ex) {
    		Util.print("An error occurred while registering a CombatLogX command.");
    		ex.printStackTrace();
    	}
    }
    
    public void forceRegisterCommand(String commandName, Class<? extends CommandExecutor> clazz) {
    	try {
			Constructor<? extends CommandExecutor> constructor;
			CommandExecutor executor;
			try {
				constructor = clazz.getDeclaredConstructor();
				executor = constructor.newInstance();
			} catch(ReflectiveOperationException ex) {
				constructor = clazz.getDeclaredConstructor(CombatLogX.class);
				executor = constructor.newInstance(this);
			}

        	CustomCommand command = new CustomCommand(commandName, executor);
        	Bukkit.getPluginManager().registerEvents(command, this);
        	
    		if(executor instanceof Listener) {
    			Listener listener = (Listener) executor;
    			PluginUtil.regEvents(listener);
    		}
    	} catch(ReflectiveOperationException ex) {
    		Util.print("An error occurred while registering a CombatLogX command.");
    		ex.printStackTrace();
    	}
    }
    
    public void forceRegisterCommand(String commandName, Class<? extends CommandExecutor> clazz, String description, String usage, String... aliases) {
    	try {
			Constructor<? extends CommandExecutor> constructor;
			CommandExecutor executor;
			try {
				constructor = clazz.getDeclaredConstructor();
				executor = constructor.newInstance();
			} catch(ReflectiveOperationException ex) {
				constructor = clazz.getDeclaredConstructor(CombatLogX.class);
				executor = constructor.newInstance(this);
			}

        	CustomCommand command = new CustomCommand(commandName, executor, description, usage, aliases);
        	Bukkit.getPluginManager().registerEvents(command, this);
        	
    		if(executor instanceof Listener) {
    			Listener listener = (Listener) executor;
    			PluginUtil.regEvents(listener);
    		}
    	} catch(ReflectiveOperationException ex) {
    		Util.print("An error occurred while registering a CombatLogX command.");
    		ex.printStackTrace();
    	}
    }
}