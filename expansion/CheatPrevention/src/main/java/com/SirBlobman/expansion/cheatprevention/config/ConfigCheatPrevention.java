package com.SirBlobman.expansion.cheatprevention.config;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.CheatPrevention;
import com.SirBlobman.expansion.cheatprevention.utility.CMIUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ConfigCheatPrevention extends Config {
	private static File FOLDER = CheatPrevention.FOLDER;
	private static File FILE = new File(FOLDER, "cheat prevention.yml");
	private static YamlConfiguration config = new YamlConfiguration();

	public static void load() {
		if (FOLDER == null) FOLDER = CheatPrevention.FOLDER;
		if (FILE == null) FILE = new File(FOLDER, "cheat prevention.yml");

		if (!FILE.exists()) copyFromJar("cheat prevention.yml", FOLDER);
		config = load(FILE);
		defaults();
	}

	public static boolean TELEPORTATION_ALLOW_DURING_COMBAT;
	public static boolean TELEPORTATION_ENDER_PEARLS_RESTART_TIMER;
	public static List<String> TELEPORTATION_ALLOWED_CAUSES;

	public static boolean FLIGHT_ALLOW_DURING_COMBAT;
	public static boolean FLIGHT_PREVENT_FALL_DAMAGE;
	public static boolean FLIGHT_ALLOW_ELYTRAS;
	public static boolean FLIGHT_ALLOW_RIPTIDE;
	public static String FLIGHT_ENABLE_PERMISSION;

	public static boolean GAMEMODE_CHANGE_WHEN_TAGGED;
	public static String GAMEMODE_GAMEMODE;

	public static boolean BLOCKED_COMMANDS_IS_WHITELIST;
	public static List<String> BLOCKED_COMMANDS_LIST;

	public static boolean INVENTORY_CLOSE_ON_COMBAT;
	public static boolean INVENTORY_PREVENT_OPENING;

	public static boolean CHAT_ALLOW_DURING_COMBAT;

	public static List<String> BLOCKED_POTIONS;

	public static boolean BLOCK_BREAKING_DURING_COMBAT;
	public static boolean BLOCK_PLACING_DURING_COMBAT;

	public static boolean ITEM_DROPPING_DURING_COMBAT;
	public static boolean ITEM_PICK_UP_DURING_COMBAT;
	public static boolean ITEM_PREVENT_TOTEMS;

	public static boolean ENTITY_PREVENT_INTERACTION;

	private static void defaults() {
		TELEPORTATION_ALLOW_DURING_COMBAT = get(config, "teleportation.allow during combat", false);
		TELEPORTATION_ENDER_PEARLS_RESTART_TIMER = get(config, "teleportation.ender pearls restart timer", false);
		TELEPORTATION_ALLOWED_CAUSES = get(config, "teleportation.allowed causes", Util.newList("ENDER_PEARL", "PLUGIN"));

		FLIGHT_ALLOW_DURING_COMBAT = get(config, "flight.allow during combat", false);
		FLIGHT_PREVENT_FALL_DAMAGE = get(config, "flight.prevent fall damage", false);
		FLIGHT_ALLOW_ELYTRAS = get(config, "flight.allow elytras", false);
		FLIGHT_ALLOW_RIPTIDE = get(config, "flight.allow riptide", false);
		FLIGHT_ENABLE_PERMISSION = get(config, "flight.enable permission", "combatlogx.flight.enable");

		GAMEMODE_CHANGE_WHEN_TAGGED = get(config, "gamemode.change", true);
		GAMEMODE_GAMEMODE = get(config, "gamemode.gamemode", "SURVIVAL").toUpperCase();

		INVENTORY_CLOSE_ON_COMBAT = get(config, "inventories.close on tag", true);
		INVENTORY_PREVENT_OPENING = get(config, "inventories.prevent opening", true);

		CHAT_ALLOW_DURING_COMBAT = get(config, "chat.allow during combat", true);

		BLOCKED_POTIONS = get(config, "potions.blocked potions", Util.newList("INVISIBILITY", "INCREASE_DAMAGE"));

		BLOCK_BREAKING_DURING_COMBAT = get(config, "blocks.allow breaking", false);
		BLOCK_PLACING_DURING_COMBAT = get(config, "blocks.allow placing", false);

		ITEM_DROPPING_DURING_COMBAT = get(config, "items.allow dropping", false);
		ITEM_PICK_UP_DURING_COMBAT = get(config, "items.allow picking up", false);
		ITEM_PREVENT_TOTEMS = get(config, "items.prevent totem usage", false);

		ENTITY_PREVENT_INTERACTION = get(config, "entities.prevent interaction", true);

		BLOCKED_COMMANDS_IS_WHITELIST = get(config, "commands.whitelist", false);
		BLOCKED_COMMANDS_LIST = get(config, "commands.commands", Util.newList("/tp", "/fly", "/gamemode"));

		fixCommands();
		detectAllAliases();
		fixAliases();

		Util.debug("[Cheat Prevention] [Command Blocker] Final Command List: " + BLOCKED_COMMANDS_LIST);
	}

	private static void fixCommands() {
		List<String> commandList = Util.newList(BLOCKED_COMMANDS_LIST);
		List<String> newCommandList = Util.newList();
		for(String command : commandList) {
			if(!command.startsWith("/")) command = "/" + command;
			newCommandList.add(command);
		}

		BLOCKED_COMMANDS_LIST = newCommandList;
	}

	private static final List<String> ALL_COMMANDS = Util.newList();
	private static final List<String> ALL_ALIASES = Util.newList();
	private static final Map<String, String> ALIAS_TO_COMMAND = Util.newMap();
	private static void detectAllAliases() {
		Map<String, String[]> allAliases = Bukkit.getCommandAliases();
		for(Entry<String, String[]> entry : allAliases.entrySet()) {
			String command = entry.getKey();
			String[] aliases = entry.getValue();
			ALL_COMMANDS.add(command);
			for(String alias : aliases) {
				ALL_ALIASES.add(alias);
				ALIAS_TO_COMMAND.put(alias, command);
			}
		}
	}

	private static void fixAliases() {
		List<String> commandList = Util.newList(BLOCKED_COMMANDS_LIST);
		List<String> newCommandList = Util.newList();

		for(String command : commandList) {
			String withoutBeginning = command.contains(" ") ? command.substring(command.indexOf(" ")) : "";
			List<String> aliases = getAliases(command);
			for(String alias : aliases) newCommandList.add("/" + alias + withoutBeginning);
		}

		BLOCKED_COMMANDS_LIST.addAll(newCommandList);
	}

	private static List<String> getAliases(String command) {
		if(command.startsWith("/")) command = command.substring(1);
		if(command.contains(" ")) command = command.substring(0, command.indexOf(" "));
		if(ALL_ALIASES.contains(command)) command = ALIAS_TO_COMMAND.getOrDefault(command, command);

		List<String> aliasList = Util.newList();
		Map<String, String[]> allAliases = Bukkit.getCommandAliases();
		String[] aliases = allAliases.getOrDefault(command, new String[0]);
		
		if(PluginUtil.isEnabled("CMI")) {
			aliasList.addAll(CMIUtil.getAliases(command));
		}

		aliasList.addAll(Util.newList(aliases));
		return aliasList;
	}
}