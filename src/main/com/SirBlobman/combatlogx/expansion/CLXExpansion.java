package com.SirBlobman.combatlogx.expansion;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.Util;

import java.io.File;

/**
 * An expansion for CombatLogX
 * 
 * @author SirBlobman
 * @see org.bukkit.plugin.java.JavaPlugin
 */
public interface CLXExpansion {
    /**
     * Code that is put here will happen when CombatLogX is loaded This should only
     * execute once!
     */
    public void enable();

    /**
     * Override this if you have to do something when CombatLogX is disabled
     */
    public default void disable() {
    }

    /**
     * <b>Example:</b> {@code "Citizens Compatability"}
     * 
     * @return The name of your expansion that will be shown to people
     */
    public String getName();

    /**
     * <b>Example</b> {@code "CompatCitizens"}
     * 
     * @return The name of your expansion that will be used for files/folders
     */
    public String getUnlocalizedName();

    /**
     * <b>Example:</b> {@code "1.0.0"}
     * 
     * @return The version of your expansion
     */
    public String getVersion();

    /**
     * <i>/plugins/CombatLogX/expansions/</i>
     * 
     * @return The folder where all the expansion jars are stored
     */
    public default File getExpansionsFolder() {
        File dot = new File(".");
        File main = dot.getAbsoluteFile().getParentFile();
        File plugins = new File(main, "plugins");
        File clx = new File(plugins, "CombatLogX");
        File exp = new File(clx, "expansions");
        exp.mkdirs();
        return exp;
    }

    /**
     * <i>/plugins/CombatLogX/expansions/{@link #getUnlocalizedName()}/</i>
     * 
     * @return The folder where data for this expansion can be stored
     */
    public default File getDataFolder() {
        File folder = getExpansionsFolder();
        File file = new File(folder, getUnlocalizedName());
        file.mkdirs();
        return file;
    }

    public default void print(Object... oo) {
        for (Object o : oo) {
            String prefix = Util.formatMessage(ConfigLang.MESSAGE_PREFIX_EXPANSION, Util.newList("{expansion}"),
                    Util.newList(getName()));
            String s = Util.str(o);
            String c = Util.color(prefix + s);
            Util.CONSOLE.sendMessage(c);
        }
    }
}