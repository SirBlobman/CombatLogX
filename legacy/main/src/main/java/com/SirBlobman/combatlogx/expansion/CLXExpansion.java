package com.SirBlobman.combatlogx.expansion;

import java.io.File;
import java.util.List;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.Util;

/**
 * An expansion for CombatLogX
 * 
 * @author SirBlobman
 * @see org.bukkit.plugin.java.JavaPlugin
 */
public interface CLXExpansion {
	/**
	 * Code that will execute when CombatLogX loads all the expansions<br/>
	 * This should only execute once!
	 */
	public void enable();
	
	/**
	 * Code that will execute when CombatLogX disabled all expansions<br/>
	 * This should only execute once and usually means the server is shutting down
	 */
	public void disable();
	
	/**
	 * Code that will execute when CombatLogX is asked to reload configs<br/>
	 * This usually means someone did "/combatlogx reload"
	 */
	public void onConfigReload();
	
	/**
	 * @return The name of your expansion that will be shown to players
	 * <br/>
	 * <b>Example:</b> {@code "Citizens Compatibility"}
	 */
	public default String getName() {return getUnlocalizedName();}
	
	/**
	 * @return The name of your expansion that will be used in debug messages, files, and folders
	 * <br/>
	 * <b>Example:</b> {@code "CompatCitizens"}
	 */
	public String getUnlocalizedName();
	
	/**
	 * @return The version of your expansion
	 * <br/>
	 * <b>Example:</b> {@code "13.1"}
	 */
	public String getVersion();
	
	/**
	 * @return The folder where all the expansion jars are stored
	 * <br/>
	 * <i>/plugins/CombatLogX/expansions/</i>
	 */
	public default File getExpansionsFolder() {
		File dot = new File(".");
		File mainFolder = dot.getAbsoluteFile().getParentFile();
		File pluginsFolder = new File(mainFolder, "plugins");
		File combatLogXFolder = new File(pluginsFolder, "CombatLogX");
		File expansionsFolder = new File(combatLogXFolder, "expansions");
		if(!expansionsFolder.exists()) expansionsFolder.mkdirs();
		return expansionsFolder;		
	}
	
	/**
	 * @return The folder where data for this expansion can be stored
	 * <br/>
	 * <b>Example:</b> <i>/plugins/CombatLogX/expansions/CompatCitizens/</i>
	 */
	public default File getDataFolder() {
		File expansionsFolder = getExpansionsFolder();
		File folder = new File(expansionsFolder, getUnlocalizedName());
		if(!folder.exists()) folder.mkdirs();
		return folder;
	}
	
	/**
	 * Print a message to console with the prefix for this expansion
	 */
	public default void print(Object... oo) {
		for(Object o : oo) {
			String format = ConfigLang.get("messages.expansion prefix");
			List<String> keys = Util.newList("{expansion}");
			List<String> vals = Util.newList(getName());
			String prefix = Util.formatMessage(format, keys, vals);
			
			String s = Util.str(o);
			String msg = Util.color(prefix + " " + s);
			Util.printNoPrefix(msg);
		}
	}
}