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
	
	public default File getExpansionsFolder() {return Expansions.EXPAND;}
}