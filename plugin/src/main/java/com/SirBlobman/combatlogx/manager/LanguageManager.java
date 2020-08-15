package com.SirBlobman.combatlogx.manager;

import java.io.File;
import java.util.List;
import java.util.Objects;

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
    public void sendLocalizedMessage(Player player, String key, Replacer... replacerArray) {
        String localeName = getLocale(player);
        String fileName = ("language/" + localeName + ".yml");
        String message = getMessage(fileName, key);

        String replace = MessageUtil.color(message);
        for(Replacer replacer : replacerArray) {
            replace = replacer.replace(replace);
        }

        player.sendMessage(replace);
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
        return (languageFile.exists() ? languageName : getLanguage());
    }
}