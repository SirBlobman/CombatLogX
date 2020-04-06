package com.SirBlobman.combatlogx.api;

import java.io.File;
import java.util.logging.Logger;

import com.SirBlobman.api.nms.MultiVersionHandler;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.listener.ICustomDeathListener;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * ICombatLogX is the interface used to implement the CombatLogX plugin
 *
 * @author SirBlobman
 */
public interface ICombatLogX {
    JavaPlugin getPlugin();
    Logger getLogger();

    File getDataFolder();

    /**
     * Register a command to CombatLogX
     * @param commandName The name of the command
     * @param executor The executor class of the command
     * @param description The description of the command
     * @param usage The usage of the command
     * @param aliases The alias list of the command
     * @see CommandExecutor
     */
    void registerCommand(String commandName, CommandExecutor executor, String description, String usage, String... aliases);

    FileConfiguration getConfig(String fileName);
    void reloadConfig(String fileName);
    void saveConfig(String fileName);
    void saveDefaultConfig(String fileName);

    YamlConfiguration getDataFile(OfflinePlayer user);
    void saveDataFile(OfflinePlayer user, YamlConfiguration config);

    String getLanguageMessage(String path);
    String getLanguageMessageColored(String path);
    String getLanguageMessageColoredWithPrefix(String path);

    ClassLoader getPluginClassLoader();
    ICombatManager getCombatManager();
    ExpansionManager getExpansionManager();
    ICustomDeathListener getCustomDeathListener();

    void sendMessage(CommandSender sender, String... messages);
    void printDebug(String message);
    
    MultiVersionHandler<?> getMultiVersionHandler();
}