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
		
		set("scoreboard.enabled", true);
		set("scoreboard.title", "Combat Log");
		set("scoreboard.time left", "Time Left");
		
		List<String> worlds = Arrays.asList("Creative", "wOrLd");
		List<String> commands = Arrays.asList("home", "tpa", "spawn", "fly");
		List<String> potions = Arrays.asList("INVISIBILITY", "UNLUCK");
		List<String> punish = Arrays.asList("ban {player} You left during combat");
		set("disabled worlds", worlds);
		set("blocked commands", commands);
		set("banned potions", potions);
		set("punish commands", punish);
		
		save();
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
		if(config.get(path) == null) config.set(path, value);
	}
}