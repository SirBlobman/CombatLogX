package com.SirBlobman.combatlogx.expansion;

import java.io.File;

/**
 * An expansion for CombatLogX
 * @author SirBlobman
 * @see org.bukkit.plugin.java.JavaPlugin
 */
public interface CLXExpansion {
	/**
	 * Code that is put here will happen when CombatLogX is loaded
	 * This should only execute once!
	 */
	public void enable();
	
	/**
	 * <b>Example:</b> {@code "NotCombatLogX"}
	 * @return The name of your expansion
	 */
	public String getName();
	
	/**
	 * <b>Example:</b> {@code "1.0.0"}
	 * @return The version of your expansion
	 */
	public String getVersion();
	
	/**
	 * <i>/plugins/CombatLogX/expansions/</i>
	 * @return The folder where all the expansion jars are stored
	 */
	public default File getExpansionsFolder() {
	    File dot = new File(".");
	    File main = dot.getAbsoluteFile().getParentFile();
	    File plugins = new File(main, "plugins");
	    File clx = new File(plugins, "CombatLogX");
	    File exp = new File(clx, "expansions");
	    return exp;
	}
	
	/**
	 * <i>/plugins/CombatLogX/expansions/{@link #getName()}</i>
	 * @return The folder where data for this expansion can be stored
	 */
	public default File getDataFolder() {
	    File folder = getExpansionsFolder();
	    File file = new File(folder, getName());
	    file.mkdirs();
	    return file;
	}
}