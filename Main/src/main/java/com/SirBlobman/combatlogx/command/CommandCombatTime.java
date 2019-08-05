package com.SirBlobman.combatlogx.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;

public class CommandCombatTime implements TabExecutor {
	@Override
	public List<String> onTabComplete(CommandSender cs, Command c, String label, String[] args) {
		String cmd = c.getName().toLowerCase();
		if(!cmd.equals("combattime")) return null;
		if(args.length != 1) return Util.newList();

		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String cmd = command.getName().toLowerCase();
		if(!cmd.equals("combattime")) return false;

		if(args.length > 0) return checkOther(sender, args);
		return checkSelf(sender);
	}

	private boolean checkOther(CommandSender sender, String[] args) {
		String targetName = args[0];
		Player target = Bukkit.getPlayer(targetName);
		if(targetName == null) {
			String message = ConfigLang.getWithPrefix("messages.commands.invalid target").replace("{target}", targetName);
			Util.sendMessage(sender, message);
			return true;
		}
		targetName = target.getName();

		if(!CombatUtil.isInCombat(target)) {
			String message = ConfigLang.getWithPrefix("messages.commands.combattime.not in combat other").replace("{target}", targetName);
			Util.sendMessage(sender, message);
			return true;
		}

		int timeLeft = CombatUtil.getTimeLeft(target);
		String timeLeftString = Integer.toString(timeLeft);

		String message = ConfigLang.getWithPrefix("messages.commands.combattime.time left other").replace("{target}", targetName).replace("{time}", timeLeftString);
		Util.sendMessage(sender, message);
		return true;
	}

	private boolean checkSelf(CommandSender sender) {
		if(!(sender instanceof Player) ) {
			String message = ConfigLang.getWithPrefix("messages.commands.not player");
			Util.sendMessage(sender, message);
			return true;
		}
		
		Player player = (Player) sender;
		if(!CombatUtil.isInCombat(player)) {
			String message = ConfigLang.getWithPrefix("messages.commands.combattime.not in combat");
			Util.sendMessage(sender, message);
			return true;
		}
		
		int timeLeft = CombatUtil.getTimeLeft(player);
		String timeLeftString = Integer.toString(timeLeft);
		
		String message = ConfigLang.getWithPrefix("messages.commands.combattime.time left").replace("{time}", timeLeftString);
		Util.sendMessage(player, message);
		return true;
	}
}