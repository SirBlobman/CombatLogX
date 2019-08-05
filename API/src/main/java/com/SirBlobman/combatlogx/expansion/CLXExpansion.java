package com.SirBlobman.combatlogx.expansion;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.combatlogx.CombatLogX;
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
     * Code that will be used when the CombatLogX is being loaded<br/>
     * This should only execute once and will trigger before CombatLogX is enabled
     */
    default void load() {}

    /**
     * Code that will execute when CombatLogX enables all the expansions<br/>
     * This should only execute once!
     */
    void enable();

    /**
     * Code that will execute when CombatLogX disabled all expansions<br/>
     * This should only execute once and usually means the server is shutting down
     */
    void disable();

    /**
     * Code that will execute when CombatLogX is asked to reload configs<br/>
     * This usually means someone did "/combatlogx reload"
     */
    void onConfigReload();

    /**
     * @return The name of your expansion that will be shown to players
     * <br/>
     * <b>Example:</b> {@code "Citizens Compatibility"}
     */
    default String getName() {
        return getUnlocalizedName();
    }

    /**
     * @return The name of your expansion that will be used in debug messages, files, and folders
     * <br/>
     * <b>Example:</b> {@code "CompatCitizens"}
     */
    String getUnlocalizedName();

    /**
     * @return The version of your expansion
     * <br/>
     * <b>Example:</b> {@code "13.1"}
     */
    String getVersion();

    /**
     * @return Should the expansion be loaded 'onLoad()'
     */
    default Boolean preload() {return false;}

    /**
     * @return If expansion is going to be loaded 'onLoad()'
     */
    default boolean isPreloaded() {return preload();}

    /**
     * @return The folder where all the expansion jars are stored
     * <br/>
     * <i>/plugins/CombatLogX/expansions/</i>
     */
    default File getExpansionsFolder() {
        File pluginFolder = JavaPlugin.getPlugin(CombatLogX.class).getDataFolder();
        File expansionsFolder = new File(pluginFolder, "expansions");
        if (!expansionsFolder.exists()) expansionsFolder.mkdirs();
        
        return expansionsFolder;
    }

    /**
     * @return The folder where data for this expansion can be stored
     * <br/>
     * <b>Example:</b> <i>/plugins/CombatLogX/expansions/CompatCitizens/</i>
     */
    default File getDataFolder() {
        File expansionsFolder = getExpansionsFolder();
        File folder = new File(expansionsFolder, getUnlocalizedName());
        if (!folder.exists()) folder.mkdirs();
        
        return folder;
    }

    /**
     * Print a message to console with the prefix for this expansion
     */
    default void print(Object... objects) {
    	for(Object object : objects) {
    		String string = Util.color(object);
    		if(string.isEmpty()) continue;
    		
    		String prefix = ConfigLang.get("messages.expansion prefix").replace("{expansion}", getName());
    		Util.printNoPrefix(prefix + " " + string);
    	}
    }
}