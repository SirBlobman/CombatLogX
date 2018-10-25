package com.SirBlobman.combatlogx.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagReason;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.UpdateUtil;
import com.SirBlobman.combatlogx.utility.Util;

public class CommandCombatLogX implements TabExecutor {
	@Override
	public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
		String cmd = c.getName().toLowerCase();
		if(cmd.equals("combatlogx")) {
			if(args.length > 0) {
				String sub = args[0].toLowerCase();
				switch (sub) {
					case "reload":
						return reload(cs);
					case "tag":
						return tag(cs, args);
					case "untag":
						return untag(cs, args);
					case "version":
						return version(cs);
					default:
						return false;
				}
			} else return false;
		} else return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender cs, Command c, String label, String[] args) {
		String cmd = c.getName().toLowerCase();
		if(cmd.equals("combatlogx")) {
			if(args.length == 1) {
				String arg = args[0];
				List<String> valid = Util.newList("reload", "tag", "untag", "version");
				return Util.getMatching(valid, arg);
			} else if(args.length == 2) {
				String sub = args[0].toLowerCase();
				if(sub.equals("tag") || sub.equals("untag")) return null;
				else return Util.newList();
			} else return Util.newList();
		} else return Util.newList();
	}

	private boolean reload(CommandSender cs) {
		String perm = "combatlogx.reload";
		if(cs.hasPermission(perm)) {
			ConfigOptions.load();
			ConfigLang.load();
			Expansions.reloadConfigs();
			
			String msg = ConfigLang.getWithPrefix("messages.commands.combatlogx.reloaded");
			Util.sendMessage(cs, msg);
			return true;
		} else {
			List<String> keys = Util.newList("{permission}");
			List<String> vals = Util.newList(perm);
			String format = ConfigLang.getWithPrefix("messages.commands.no permission");
			String error = Util.formatMessage(format, keys, vals);
			Util.sendMessage(cs, error);
			return true;
		}
	}
	
	private boolean tag(CommandSender cs, String[] args) {
		String perm = "combatlogx.tag";
		if(cs.hasPermission(perm)) {
			if(args.length > 1) {
				String target = args[1];
				Player t = Bukkit.getPlayer(target);
				if(t != null) {
					CombatUtil.tag(t, null, TagType.UNKNOWN, TagReason.UNKNOWN);
					List<String> keys = Util.newList("{target}");
					List<?> vals = Util.newList(t.getName());
					String format = ConfigLang.getWithPrefix("messages.commands.combatlogx.tag");
					String msg = Util.formatMessage(format, keys, vals);
					Util.sendMessage(cs, msg);
					return true;
				} else {
					List<String> keys = Util.newList("{target}");
					List<?> vals = Util.newList(target);
					String format = ConfigLang.getWithPrefix("messages.commands.invalid target");
					String error = Util.formatMessage(format, keys, vals);
					Util.sendMessage(cs, error);
					return true;
				}
			} else return false;
		} else {
			List<String> keys = Util.newList("{permission}");
			List<String> vals = Util.newList(perm);
			String format = ConfigLang.getWithPrefix("messages.commands.no permission");
			String error = Util.formatMessage(format, keys, vals);
			Util.sendMessage(cs, error);
			return true;
		}
	}
	
	private boolean untag(CommandSender cs, String[] args) {
		String perm = "combatlogx.untag";
		if(cs.hasPermission(perm)) {
			if(args.length > 1) {
				String target = args[1];
				Player t = Bukkit.getPlayer(target);
				if(t != null) {
					if(CombatUtil.isInCombat(t)) {
						CombatUtil.untag(t, UntagReason.EXPIRE);
						List<String> keys = Util.newList("{target}");
						List<?> vals = Util.newList(t.getName());
						String format = ConfigLang.getWithPrefix("messages.commands.combatlogx.untag");
						String msg = Util.formatMessage(format, keys, vals);
						Util.sendMessage(cs, msg);
						return true;
					} else {
						String error = ConfigLang.getWithPrefix("messages.commands.combatlogx.not in combat");
						Util.sendMessage(cs, error);
						return true;
					}
				} else {
					List<String> keys = Util.newList("{target}");
					List<?> vals = Util.newList(target);
					String format = ConfigLang.getWithPrefix("messages.commands.invalid target");
					String error = Util.formatMessage(format, keys, vals);
					Util.sendMessage(cs, error);
					return true;
				}
			} else return false;
		} else {
			List<String> keys = Util.newList("{permission}");
			List<String> vals = Util.newList(perm);
			String format = ConfigLang.getWithPrefix("messages.commands.no permission");
			String error = Util.formatMessage(format, keys, vals);
			Util.sendMessage(cs, error);
			return true;
		}
	}
	
	private boolean version(CommandSender cs) {
		String perm = "combatlogx.version";
		if(cs.hasPermission(perm)) {
			Util.sendMessage(cs, "Getting plugin versions...");
			SchedulerUtil.runNowAsync(() -> {
				String pversion = UpdateUtil.getPluginVersion();
				String sversion = UpdateUtil.getSpigotVersion();
				
				String[] msg = Util.color(
				    "&f&lCombatLogX by SirBlobman",
				    " ",
					"&f&lYour Version: &7v" + pversion,
					"&f&lLatest Version: &7v" + sversion,
					" ",
					"&7&oGetting expansion versions...",
					" "
				);
				Util.sendMessage(cs, msg);
				
				List<CLXExpansion> expansions = Expansions.getExpansions();
				if(expansions.isEmpty()) {
					String error = Util.color("  &f&lYou do not have any expansions.");
					Util.sendMessage(cs, error);
				} else expansions.forEach(clxe -> {
					String name = clxe.getName();
					String version = clxe.getVersion();
					String msg1 = Util.color("  &f&l" + name + " &7v" + version);
					Util.sendMessage(cs, msg1);
				});
			});
			return true;
		} else {
			List<String> keys = Util.newList("{permission}");
			List<String> vals = Util.newList(perm);
			String format = ConfigLang.getWithPrefix("messages.commands.no permission");
			String error = Util.formatMessage(format, keys, vals);
			Util.sendMessage(cs, error);
			return true;
		}
	}
}