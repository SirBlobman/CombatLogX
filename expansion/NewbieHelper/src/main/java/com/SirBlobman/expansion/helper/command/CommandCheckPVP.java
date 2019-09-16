package com.SirBlobman.expansion.helper.command;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CommandCheckPVP implements CommandExecutor, Listener {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("combatlogx.command.pvpcheck")) {
			String message = ConfigLang.getWithPrefix("messages.commands.no permission").replace("{permission}", "combatlogx.command.pvpcheck");
			Util.sendMessage(sender, message);
			return true;
		}

		if(args.length < 1 && !(sender instanceof Player)) {
			String message = ConfigLang.getWithPrefix("messages.commands.not player");
			Util.sendMessage(sender, message);
			return true;
		}

		String targetName = args.length < 1 ? sender.getName() : args[0];
		Player target = Bukkit.getPlayer(targetName);
		if(target == null) {
			String message = ConfigLang.getWithPrefix("messages.commands.invalid target").replace("{target}", targetName);
			Util.sendMessage(sender, message);
			return true;
		}

		targetName = target.getName();
		boolean pvp = CommandTogglePVP.isPVPEnabled(target);

		String messageFormat = ConfigLang.getWithPrefix("messages.expansions.newbie helper.check.format");
		String setting = ConfigLang.get("messages.expansions.newbie helper.check.setting " + (pvp ? "enabled" : "disabled"));

		Util.sendMessage(sender, messageFormat.replace("{target}", targetName).replace("{setting}", setting));
		return true;
	}
}