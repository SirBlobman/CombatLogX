package com.SirBlobman.combatlogx.api;

import java.io.File;
import java.util.logging.Logger;

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
    public JavaPlugin getPlugin();
    public Logger getLogger();

    public File getDataFolder();

    /**
     * Register a command to CombatLogX
     * @param commandName The name of the command
     * @param executor The executor class of the command
     * @param description The description of the command
     * @param usage The usage of the command
     * @param aliases The alias list of the command
     * @see CommandExecutor
     */
    public void registerCommand(String commandName, CommandExecutor executor, String description, String usage, String... aliases);

    public FileConfiguration getConfig(String fileName);
    public void reloadConfig(String fileName);
    public void saveConfig(String fileName);
    public void saveDefaultConfig(String fileName);

    public YamlConfiguration getDataFile(OfflinePlayer user);
    public void saveDataFile(OfflinePlayer user, YamlConfiguration config);

    public String getLanguageMessage(String path);
    public String getLanguageMessageColored(String path);
    public String getLanguageMessageColoredWithPrefix(String path);

    public ClassLoader getPluginClassLoader();
    public ICombatManager getCombatManager();
    public ICustomDeathListener getCustomDeathListener();

    public void sendMessage(CommandSender sender, String... messages);
}