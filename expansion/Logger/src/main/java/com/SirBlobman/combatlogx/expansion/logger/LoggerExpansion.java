package com.SirBlobman.combatlogx.expansion.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.expansion.logger.listener.ListenerLogger;

public class LoggerExpansion extends Expansion {
    public LoggerExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        saveDefaultConfig("logger.yml");
    }

    @Override
    public void reloadConfig() {
        reloadConfig("logger.yml");
    }

    @Override
    public void onEnable() {
        ICombatLogX plugin = getPlugin();
        JavaPlugin javaPlugin = plugin.getPlugin();
        ListenerLogger listener = new ListenerLogger(this);

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(listener, javaPlugin);
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    private static final Pattern FILENAME_REGEX = Pattern.compile("[^\\w.\\-]");
    public String getLogFileName() {
        FileConfiguration config = getConfig("logger.yml");
        if(config == null) return "logger.log";

        String fileNameOption = config.getString("log-file-info.file-name");
        if(fileNameOption == null) fileNameOption = "logger";

        String fileExtraFormatOption = config.getString("log-file-info.file-extra.format");
        if(fileExtraFormatOption == null) fileExtraFormatOption = "yyyy.MM.dd";

        String fileExtensionOption = config.getString("log-file-info.file-extension");
        if(fileExtensionOption == null) fileExtensionOption = "log";

        SimpleDateFormat format = new SimpleDateFormat(fileExtraFormatOption);
        Date currentDate = new Date(System.currentTimeMillis());
        String fileNameExtra = format.format(currentDate);

        String preFileName = (fileNameOption + "-" + fileNameExtra + "." + fileExtensionOption);
        Matcher matcher = FILENAME_REGEX.matcher(preFileName);
        return matcher.replaceAll("_");
    }
}