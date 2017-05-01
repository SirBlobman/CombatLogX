package com.SirBlobman.combatlog;

import java.io.File;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.combatlog.command.CommandCombatTime;
import com.SirBlobman.combatlog.command.CommandReload;
import com.SirBlobman.combatlog.utility.Util;

public class CombatLog extends JavaPlugin {
	public static CombatLog instance;
	public static File folder;
	
	@Override
	public void onEnable() {
		instance = this;
		folder = getDataFolder();
		Util.enable();
		c("clreload", new CommandReload());
		c("ct", new CommandCombatTime());
		Util.broadcast("&2Enabled");
	}
	
	@Override
	public void onDisable() {
		Util.broadcast("&4Disabled");
	}
	
	private void c(String cmd, CommandExecutor ex) {
		PluginCommand c = getCommand(cmd);
		c.setExecutor(ex);
	}
}