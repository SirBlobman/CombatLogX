package com.github.sirblobman.combatlogx.configuration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.CombatPlugin;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import org.zeroturnaround.zip.ZipUtil;

public final class ConfigurationChecker {
    private final ICombatLogX plugin;

    public ConfigurationChecker(@NotNull CombatPlugin plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
    }

    public void checkVersion() {
        Logger logger = getLogger();
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                logger.info("Configuration does not exist yet, no major changes necessary.");
                return;
            }

            FilenameFilter ymlOnly = (folder, fileName) -> fileName.endsWith(".yml");
            File[] yamlFileArray = dataFolder.listFiles(ymlOnly);
            if (yamlFileArray == null || yamlFileArray.length == 0) {
                logger.info("Configuration does not exist yet, no major changes necessary.");
                return;
            }

            File configFile = new File(dataFolder, "config.yml");
            if (!configFile.exists()) {
                logger.info("Configuration does not exist yet, no major changes necessary.");
                return;
            }

            File punishConfigFile = new File(dataFolder, "punish.yml");
            if (!punishConfigFile.exists()) {
                makeBackup();
                return;
            }

            YamlConfiguration configuration = new YamlConfiguration();
            configuration.load(configFile);

            String generatedByVersion = configuration.getString("generated-by-version");
            if (generatedByVersion == null || !generatedByVersion.startsWith("11.")) {
                makeBackup();
                return;
            }

            logger.info("Configuration version is recent, no major changes necessary.");
        } catch (IOException | InvalidConfigurationException ex) {
            logger.log(Level.WARNING, "An error occurred while checking the configuration version:", ex);
        }
    }

    private ICombatLogX getPlugin() {
        return this.plugin;
    }

    private Logger getLogger() {
        ICombatLogX plugin = getPlugin();
        return plugin.getLogger();
    }

    private void makeBackup() throws IOException {
        Logger logger = getLogger();
        logger.warning("Configuration version is outdated, backing up files...");

        ICombatLogX plugin = getPlugin();
        File dataFolder = plugin.getDataFolder();
        File pluginsFolder = dataFolder.getParentFile();

        File backupFile = new File(pluginsFolder, "CombatLogX-" + System.currentTimeMillis() + ".backup.zip");
        ZipUtil.pack(dataFolder, backupFile);

        logger.warning("Configuration version is outdated, deleting old files...");
        deleteFile(dataFolder);
    }

    private void deleteFile(File parentFile) throws IOException {
        String parentFileName = parentFile.getName();
        if (parentFile.isDirectory() && parentFileName.equals("expansions")) {
            return;
        }

        if (parentFileName.endsWith(".jar")) {
            return;
        }

        if (!parentFile.isDirectory()) {
            if (!parentFile.delete()) {
                throw new IOException("Failed to delete a a file.");
            }

            return;
        }

        File[] fileArray = parentFile.listFiles();
        if (fileArray == null || fileArray.length == 0) {
            if (!parentFile.delete()) {
                throw new IOException("Failed to delete a a file.");
            }

            return;
        }

        for (File file : fileArray) {
            String fileName = file.getName();
            if (file.isDirectory() && fileName.equals("expansions")) {
                continue;
            }

            if (fileName.endsWith(".jar")) {
                continue;
            }

            deleteFile(file);
        }

        File[] fileArray2 = parentFile.listFiles();
        if (fileArray2 == null || fileArray2.length == 0) {
            if (!parentFile.delete()) {
                throw new IOException("Failed to delete a a file.");
            }
        }
    }
}
