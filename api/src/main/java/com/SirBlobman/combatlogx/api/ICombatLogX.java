package com.SirBlobman.combatlogx.api;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.api.configuration.ConfigurationManager;
import com.SirBlobman.api.configuration.PlayerDataManager;
import com.SirBlobman.api.language.LanguageManager;
import com.SirBlobman.api.nms.MultiVersionHandler;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;

public interface ICombatLogX {
    JavaPlugin getPlugin();
    Logger getLogger();
    File getDataFolder();
    ClassLoader getPluginClassLoader();

    YamlConfiguration getConfig(String fileName);
    void reloadConfig(String fileName);
    void saveConfig(String fileName);
    void saveDefaultConfig(String fileName);

    YamlConfiguration getData(OfflinePlayer player);
    void saveData(OfflinePlayer player);

    MultiVersionHandler getMultiVersionHandler();
    ConfigurationManager getConfigurationManager();
    PlayerDataManager getPlayerDataManager();
    LanguageManager getLanguageManager();

    ExpansionManager getExpansionManager();
    ICombatManager getCombatManager();

    void printDebug(String... messageArray);

    default String getMessageColoredWithPrefix(CommandSender sender, String key) {
        LanguageManager languageManager = getLanguageManager();
        String message = languageManager.getMessageColored(sender, key);
        if(message.isEmpty()) return "";

        String prefix = languageManager.getMessageColored(sender, "prefix");
        return (prefix.isEmpty() ? message : (prefix + " " + message));
    }

    default void sendMessage(CommandSender sender, String... messageArray) {
        for(String message : messageArray) {
            if(message == null || message.isEmpty()) continue;
            sender.sendMessage(message);
        }
    }
}