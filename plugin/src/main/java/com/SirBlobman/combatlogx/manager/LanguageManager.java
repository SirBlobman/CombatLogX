package com.SirBlobman.combatlogx.manager;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.api.shaded.configuration.ConfigManager;
import com.SirBlobman.combatlogx.api.shaded.nms.VersionUtil;
import com.SirBlobman.combatlogx.api.shaded.utility.MessageUtil;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;
import com.SirBlobman.combatlogx.api.utility.Replacer;

public final class LanguageManager implements ILanguageManager {
    private final CombatLogX plugin;
    public LanguageManager(CombatLogX plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");
    }
    
    @Override
    public String getLanguage() {
        ConfigManager<?> configManager = this.plugin.getConfigManager();
        YamlConfiguration config = configManager.getConfig("config.yml");
        String languageName = config.getString("language");
        if(languageName == null) return "en_us";
        
        File pluginFolder = this.plugin.getDataFolder();
        File languageFolder = new File(pluginFolder, "language");
        File languageFile = new File(languageFolder, languageName + ".yml");
        return (languageFile.exists() ? languageName : "en_us");
    }
    
    @Override
    public String getMessage(String key) {
        String languageName = getLanguage();
        String fileName = ("language/" + languageName + ".yml");
        return getMessage(fileName, key);
    }

    @Override
    public String getLocalizedMessage(Player player, String key) {
        String localeName = getLocale(player);
        String fileName = ("language/" + localeName + ".yml");
        return getMessage(fileName, key);
    }

    @Override
    public void sendLocalizedMessage(Player player, String key, Replacer... replacerArray) {
        String message = getLocalizedMessage(player, key);
        String replace = MessageUtil.color(message);
        for(Replacer replacer : replacerArray) {
            replace = replacer.replace(replace);
        }

        player.sendMessage(replace);
    }
    
    public void reloadConfig() {
        String languageName = getLanguage();
        String fileName = ("language/" + languageName + ".yml");
    
        ConfigManager<?> configManager = this.plugin.getConfigManager();
        configManager.reloadConfig(fileName);
    
        Collection<? extends Player> onlinePlayerList = Bukkit.getOnlinePlayers();
        for(Player player : onlinePlayerList) {
            String localeName = getLocale(player);
            String localeFile = ("language/" + localeName + ".yml");
            configManager.reloadConfig(localeFile);
        }
    }

    private String getMessage(String fileName, String key) {
        ConfigManager<?> configManager = this.plugin.getConfigManager();
        YamlConfiguration config = configManager.getConfig(fileName);

        if(config.isList(key)) {
            List<String> messageList = config.getStringList(key);
            return String.join("\n", messageList);
        }

        String message = config.getString(key);
        return (message == null ? "" : message);
    }

    private String getLocale(Player player) {
        int minorVersion = VersionUtil.getMinorVersion();
        if(minorVersion < 12) return getLanguage();
        String languageName = player.getLocale();
        
        File pluginFolder = this.plugin.getDataFolder();
        File languageFolder = new File(pluginFolder, "language");
        File languageFile = new File(languageFolder, languageName + ".yml");
        if(languageFile.exists()) return languageName;
        
        this.plugin.printDebug("Could not find language file '" + languageName + ".yml' for player. Using default language from config.yml...");
        return getLanguage();
    }
}