package com.SirBlobman.combatlog.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlog.Combat;
import com.SirBlobman.combatlog.config.Config;
import com.SirBlobman.combatlog.utility.Util;

public class CommandCombatTime implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
		String cmd = c.getName().toLowerCase();
		if(cmd.equals("combattime")) {
			if(cs instanceof Player) {
				Player p = (Player) cs;
				int time = Combat.timeLeft(p);
				if(time > 0) {
					String msg = Util.format(Config.MSG_PREFIX + Config.MSG_IN_COMBAT, time);
					p.sendMessage(msg);
				} else {
					String msg = Util.format(Config.MSG_PREFIX + Config.MSG_NOT_IN_COMBAT);
					p.sendMessage(msg);
				}
				return true;
			} else {
				String error = Util.color("Only players can do this command!");
				cs.sendMessage(error);
				return true;
			}
		}
		return false;
	}
}