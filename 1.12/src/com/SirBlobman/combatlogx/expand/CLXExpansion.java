package com.SirBlobman.combatlogx.expand;

public interface CLXExpansion {
	/**
	 * Put code that you want to happen here
	 * This will be executed once, when CombatLogX loads onto the server
	 */
	public void enable();
	
	/**
	 * @return Name of the expansion
	 */
	public String getName();
	
	/**
	 * @return Version of the expansion (e.g: 1.0.0)
	 */
	public String getVersion();
}