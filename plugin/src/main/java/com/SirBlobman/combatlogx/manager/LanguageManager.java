package com.SirBlobman.combatlogx.manager;

import java.io.File;
import java.util.List;
import java.util.Objects;

import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.api.shaded.configuration.ConfigManager;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;

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
        if(languageName == null) return "en_US";
        
        File pluginFolder = this.plugin.getDataFolder();
        File languageFolder = new File(pluginFolder, "language");
        File languageFile = new File(languageFolder, languageName + ".yml");
        return (languageFile.exists() ? languageName : "en_US");
    }
    
    @Override
    public String getMessage(String key) {
        String languageName = getLanguage();
        String fileName = ("language/" + languageName + ".lang.yml");
        ConfigManager<?> configManager = this.plugin.getConfigManager();
        YamlConfiguration config = configManager.getConfig(fileName);
        
        if(config.isList(key)) {
            List<String> messageList = config.getStringList(key);
            return String.join("\n", messageList);
        }
        
        String message = config.getString(key);
        return (message == null ? "" : message);
    }
}