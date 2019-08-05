package com.SirBlobman.combatlogx;

import java.io.File;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class CombatLogX extends JavaPlugin {
    public static CombatLogX INSTANCE;
    public static File FOLDER;
    public static ClassLoader CLASS_LOADER;

    @Override
    public void onEnable() {
        throw new UnsupportedOperationException();
    }
    
    public void forceRegisterCommand(String commandName, Class<? extends CommandExecutor> clazz, String description, String usage, String... aliases) {
    	throw new UnsupportedOperationException();
    }
}