package com.SirBlobman.combatlogx;

import com.SirBlobman.combatlogx.command.CommandCombatTime;
import com.SirBlobman.combatlogx.command.CommandReload;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CombatLogX extends JavaPlugin {
	public static CombatLogX instance;
	public static File folder;
	
	@Override
	public void onEnable() {
		instance = this;
		folder = getDataFolder();
		Bukkit.getServer().getScheduler().runTaskLater(this, new Runnable() {
			@Override
			public void run() {
				Util.enable();
				c("clreload", new CommandReload());
				c("ct", new CommandCombatTime());
				Util.broadcast("&2Enabled");
			}
		}, 0L);
	}
	
	private void c(String cmd, CommandExecutor ex) {
		PluginCommand c = getCommand(cmd);
		c.setExecutor(ex);
	}
}