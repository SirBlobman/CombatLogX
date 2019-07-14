package com.SirBlobman.combatlogx.expansion;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.Util;

import java.io.File;
import java.util.List;

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
        File dot = new File(".");
        File mainFolder = dot.getAbsoluteFile().getParentFile();
        File pluginsFolder = new File(mainFolder, "plugins");
        File combatLogXFolder = new File(pluginsFolder, "CombatLogX");
        File expansionsFolder = new File(combatLogXFolder, "expansions");
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
    default void print(Object... oo) {
        for (Object o : oo) {
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