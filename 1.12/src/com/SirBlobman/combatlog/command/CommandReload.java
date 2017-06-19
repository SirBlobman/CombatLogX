package com.SirBlobman.combatlog.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.SirBlobman.combatlog.config.Config;
import com.SirBlobman.combatlog.utility.Util;

public class CommandReload implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
		String cmd = c.getName().toLowerCase();
		if(cmd.equals("clreload")) {
			Config.reload();
			String msg = Util.color(Config.MSG_PREFIX + Config.MSG_RELOAD_CONFIG);
			cs.sendMessage(msg);
			return true;
		} else return false;
	}
}