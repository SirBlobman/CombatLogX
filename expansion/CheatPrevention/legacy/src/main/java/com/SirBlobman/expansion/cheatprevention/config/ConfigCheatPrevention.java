package com.SirBlobman.expansion.cheatprevention.config;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;

import java.util.List;
import java.util.Map;

public class ConfigCheatPrevention extends Config {
	public static void load() {
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}

	private static void fixCommands() {
		throw new UnsupportedOperationException();
	}

	private static final List<String> ALL_COMMANDS = Util.newList();
	private static final List<String> ALL_ALIASES = Util.newList();
	private static final Map<String, String> ALIAS_TO_COMMAND = Util.newMap();
	private static void detectAllAliases() {
		throw new UnsupportedOperationException();
	}

	private static void fixAliases() {
		throw new UnsupportedOperationException();
	}

	private static List<String> getAliases(String command) {
		throw new UnsupportedOperationException();
	}
}