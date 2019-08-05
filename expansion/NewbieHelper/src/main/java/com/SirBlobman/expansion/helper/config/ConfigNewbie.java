package com.SirBlobman.expansion.helper.config;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.helper.NewbieHelper;

public class ConfigNewbie extends Config {
	// Start Main Config File
	
	private static YamlConfiguration mainConfig = new YamlConfiguration();
	public static void load() {
		File folder = NewbieHelper.FOLDER;
		File file = new File(folder, "newbie helper.yml");
		if(!file.exists()) copyFromJar("newbie helper.yml", folder);
		
		mainConfig = load(file);
	}
	
	public static <O> O getOption(String path, O defaultValue) {
		return get(mainConfig, path, defaultValue);
	}
	
	// End Main Config File
	
	// Start User Data Files
	
	private static YamlConfiguration loadUser(OfflinePlayer player) {
		if(player == null) return null;
		UUID uuid = player.getUniqueId();
		String uuidString = uuid.toString();
		
		File folder = NewbieHelper.FOLDER;
		File folder_users = new File(folder, "users");
		File file = new File(folder_users, uuidString + ".data.yml");
		
		try {
			if(!file.exists()) {
				folder_users.mkdirs();
				file.createNewFile();
			}
			
			YamlConfiguration config = new YamlConfiguration();
			config.load(file);
			return config;
		} catch(IOException | InvalidConfigurationException ex) {
			Util.log("[Newbie Helper] An error occurred while loading a user data file.");
			ex.printStackTrace();
			return new YamlConfiguration();
		}
	}
	
	private static void saveUser(OfflinePlayer player, YamlConfiguration config) {
		if(player == null || config == null) return;
		UUID uuid = player.getUniqueId();
		String uuidString = uuid.toString();
		
		File folder = NewbieHelper.FOLDER;
		File folder_users = new File(folder, "users");
		File file = new File(folder_users, uuidString + ".data.yml");
		
		try {
			if(!file.exists()) {
				folder_users.mkdirs();
				file.createNewFile();
			}
			
			config.save(file);
		} catch(IOException ex) {
			Util.log("[Newbie Helper] An error occurred while saving a user data file.");
			ex.printStackTrace();
		}
	}
	
	public static <O> O getData(OfflinePlayer player, String path, O defaultValue) {
		YamlConfiguration config = loadUser(player);
		return get(config, path, defaultValue);
	}
	
	public static <O> void setData(OfflinePlayer player, String path, O value) {
		YamlConfiguration config = loadUser(player);
		config.set(path, value);
		saveUser(player, config);
	}
	
	// End User Data Files
}