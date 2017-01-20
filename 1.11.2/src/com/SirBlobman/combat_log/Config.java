package com.SirBlobman.combat_log;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config
{
	private static File folder = CombatLog.instance.getDataFolder();
	private static File file = new File(folder, "combat.yml");
	private static YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	
	/*Config Values*/
	public static int TIMER;
	public static boolean SCOREBOARD_ENABLED;
	public static boolean UPDATE_CHECKER;
	public static boolean ACTION_BAR;
	public static boolean BOSS_BAR;
	public static boolean SELF_COMBAT;
	public static boolean MOBS_COMBAT;
	public static boolean REMOVE_POTIONS;
	public static boolean PREVENT_FLIGHT;
	public static boolean CHANGE_GAMEMODE;
	public static boolean PUNISH_LOGGERS;
	public static List<String> DISABLED_WORLDS;
	public static List<String> BLOCKED_COMMANDS;
	public static List<String> PUNISH_COMMANDS;
	public static List<String> BANNED_POTIONS;
	public static List<String> SCOREBOARD_LIST;
	/*Config Values*/
	
	public static YamlConfiguration load()
	{
		if(!file.exists()) save();
		try{config.load(file); defaults(); return config;}
		catch(Exception ex) 
		{
			System.out.println("Error loading config:");
			System.out.println(ex.getMessage());
			return null;
		}
	}
	
	public static void save()
	{
		if(!file.exists())
		{
			try{folder.mkdirs(); file.createNewFile(); defaults();}
			catch(Exception ex) {System.out.println("Error creating config! " + ex.getMessage()); return;}
		}
		try{config.save(file);}
		catch(Exception ex) {System.out.println("Error saving config! " + ex.getMessage());}
	}
	
	private static void defaults()
	{
		set("options.timer", 30);
		set("options.update checker", true);
		set("options.action bar", true);
		set("options.boss bar", true);
		set("options.self combat", true);
		set("options.mobs combat", false);
		set("options.remove potions", false);
		set("options.prevent flight", true);
		set("options.change gamemode", true);
		set("options.punish loggers", true);
		
		
		set("messages.prefix", "[CombatLog] &f");
		set("messages.target", "&5%s&r attacked you! You are now in combat!");
		set("messages.attack", "You attacked &5%s&r! you are now in combat!");
		set("messages.expire", "&eYou are no longer in combat!");
		set("messages.blocked", "&6You can't do &c%s&6 during combat!");
		set("messages.in combat", "You are still in combat for &b%s seconds");
		set("messages.not in combat", "You are not in combat");
		set("messages.quit", "&5%s&r left during combat!");
		set("messages.action bar", "&3Combat >> &2%s seconds &eleft");
		set("messages.boss bar", "&3Combat >> &2%s seconds");
		set("messages.reload config", "The config was reloaded");

		set("scoreboard.enabled", true);
		set("scoreboard.title", "Combat Log");
		set("scoreboard.items", Arrays.asList("Time Left: {time_left}", "Enemy: {enemy_name}", "Enemy Health: {enemy_health}"));
		
		List<String> worlds = Arrays.asList("Creative", "wOrLd");
		List<String> commands = Arrays.asList("home", "tpa", "spawn", "fly");
		List<String> potions = Arrays.asList("INVISIBILITY", "UNLUCK");
		List<String> punish = Arrays.asList("ban {player} You left during combat");
		set("disabled worlds", worlds);
		set("blocked commands", commands);
		set("banned potions", potions);
		set("punish commands", punish);
		save();
		
		TIMER = config.getInt("options.timer");
		UPDATE_CHECKER = config.getBoolean("options.update checker");
		ACTION_BAR = config.getBoolean("options.action bar");
		BOSS_BAR = config.getBoolean("options.boss bar");
		SELF_COMBAT = config.getBoolean("options.self combat");
		MOBS_COMBAT = config.getBoolean("options.mobs combat");
		REMOVE_POTIONS = config.getBoolean("options.remove potions");
		PREVENT_FLIGHT = config.getBoolean("options.prevent flight");
		CHANGE_GAMEMODE = config.getBoolean("options.change gamemode");
		PUNISH_LOGGERS = config.getBoolean("options.punish loggers");
		DISABLED_WORLDS = config.getStringList("disabled worlds");
		BLOCKED_COMMANDS = config.getStringList("blocked commands");
		BANNED_POTIONS = config.getStringList("banned potions");
		PUNISH_COMMANDS = config.getStringList("punish commands");
		SCOREBOARD_LIST = config.getStringList("scoreboard.items");
		SCOREBOARD_ENABLED = config.getBoolean("scoreboard.enabled");
	}
	
	public static String option(String path, Object... format)
	{
		load();
		String o = config.getString(path);
		String c = ChatColor.translateAlternateColorCodes('&', o);
		String f = String.format(c, format);
		return f;
	}
	
	private static void set(String path, Object value)
	{
		boolean b = (config.get(path) == null);
		if(b) config.set(path, value);
	}
}