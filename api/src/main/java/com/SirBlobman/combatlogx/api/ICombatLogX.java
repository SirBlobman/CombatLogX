package com.SirBlobman.combatlogx.api;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.api.nms.MultiVersionHandler;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.listener.ICustomDeathListener;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;

/**
 * ICombatLogX is the interface used to implement the CombatLogX plugin
 *
 * @author SirBlobman
 */
public interface ICombatLogX {
    JavaPlugin getPlugin();
    Logger getLogger();
    File getDataFolder();

    YamlConfiguration getConfig(String fileName);
    void reloadConfig(String fileName);
    void saveConfig(String fileName);
    void saveDefaultConfig(String fileName);

    YamlConfiguration getDataFile(OfflinePlayer user);
    void saveDataFile(OfflinePlayer user);

    ClassLoader getPluginClassLoader();
    ICustomDeathListener getCustomDeathListener();
    MultiVersionHandler<?> getMultiVersionHandler();
    
    ICombatManager getCombatManager();
    ILanguageManager getLanguageManager();
    ExpansionManager getExpansionManager();
    
    /**
     * Register a command to CombatLogX
     * @param commandName The name of the command
     * @param executor The executor class of the command
     * @param description The description of the command
     * @param usage The usage of the command
     * @param aliasArray An array of aliases for the command
     * @see CommandExecutor
     */
    void registerCommand(String commandName, CommandExecutor executor, String description, String usage, String... aliasArray);
    
    default void printDebug(String... messageArray) {
        Logger logger = getLogger();
        for(String message : messageArray) {
            if(message == null) continue;
            logger.info("[Debug] " + message);
        }
    }
}