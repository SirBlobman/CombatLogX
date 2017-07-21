package com.SirBlobman.combatlogx.command;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandReload implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
		String cmd = c.getName().toLowerCase();
		if(cmd.equals("clreload")) {
			Config.loadC();
			Config.loadL();
			String msg = Util.color(Config.MESSAGE_RELOAD_CONFIG);
			Util.sendMessage(cs, msg);
			return true;
		} else return false;
	}
}