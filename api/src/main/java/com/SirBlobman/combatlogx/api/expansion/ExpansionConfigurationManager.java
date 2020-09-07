package com.SirBlobman.combatlogx.api.expansion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.api.utility.Validate;

public final class ExpansionConfigurationManager {
    private final Expansion expansion;
    private final Map<String, YamlConfiguration> configurationMap;
    public ExpansionConfigurationManager(Expansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
        this.configurationMap = new HashMap<>();
    }

    /**
     * @return The {@link Expansion} that will be used to create/save the configs
     */
    public Expansion getExpansion() {
        return this.expansion;
    }

    /**
     * Copies the default configuration from the jar if it does not already exist.
     * @param fileName The name of the configuration to copy
     */
    public void saveDefault(String fileName) {
        File absoluteFile = getAbsoluteFile(fileName);
        saveDefault(fileName, absoluteFile);
    }

    /**
     * @param fileName The name of the configuration to get
     * @return A configuration from memory. If the configuration is not in memory it will be loaded
     */
    public YamlConfiguration get(String fileName) {
        File absoluteFile = getAbsoluteFile(fileName);
        String absoluteFileName = absoluteFile.getAbsolutePath();

        YamlConfiguration configuration = this.configurationMap.getOrDefault(absoluteFileName, null);
        if(configuration == null) {
            reload(fileName);
            configuration = this.configurationMap.getOrDefault(absoluteFileName, new YamlConfiguration());
        }

        return configuration;
    }

    /**
     * Save a configuration from memory to a file
     * @param fileName The name of the configuration to save
     */
    public void save(String fileName) {
        File absoluteFile = getAbsoluteFile(fileName);
        String absoluteFileName = absoluteFile.getAbsolutePath();

        YamlConfiguration configuration = this.configurationMap.getOrDefault(absoluteFileName, null);
        if(configuration == null) return;

        try {
            configuration.save(absoluteFile);
        } catch(IOException ex) {
            Logger logger = this.expansion.getLogger();
            logger.log(Level.WARNING, "Failed to save config '" + fileName + "' because an I/O error occurred:", ex);
        }
    }

    /**
     * Reload a configuration from a file into memory
     * @param fileName The name of the configuration to reload
     */
    public void reload(String fileName) {
        File absoluteFile = getAbsoluteFile(fileName);
        String absoluteFilePath = absoluteFile.getAbsolutePath();
        if(!absoluteFile.exists()) {
            Logger logger = this.expansion.getLogger();
            logger.warning("Failed to reload config '" + fileName + "' because the file does not exist!");
            return;
        }

        try {
            YamlConfiguration configuration = new YamlConfiguration();
            InputStream jarStream = this.expansion.getResource(fileName);
            if(jarStream != null) {
                InputStreamReader jarStreamReader = new InputStreamReader(jarStream, StandardCharsets.UTF_8);
                YamlConfiguration jarConfiguration = new YamlConfiguration();
                jarConfiguration.load(jarStreamReader);
                configuration.setDefaults(jarConfiguration);
            }

            configuration.load(absoluteFile);
            this.configurationMap.put(absoluteFilePath, configuration);
        } catch(IOException | InvalidConfigurationException ex) {
            Logger logger = this.expansion.getLogger();
            logger.log(Level.WARNING, "Failed to reload config '" + fileName + "' because an I/O error occurred:", ex);
        }
    }

    private File getAbsoluteFile(String fileName) {
        Validate.notEmpty(fileName, "fileName cannot be null or empty!");
        File pluginFolder = this.expansion.getDataFolder();
        File relativeFile = new File(pluginFolder, fileName);
        return relativeFile.getAbsoluteFile();
    }

    private void saveDefault(String jarName, File absoluteFile) {
        Validate.notEmpty(jarName, "jarName cannot be null or empty!");
        Validate.notNull(absoluteFile, "absoluteFile cannot be null!");
        if(absoluteFile.exists()) return;

        InputStream jarStream = this.expansion.getResource(jarName);
        if(jarStream == null) {
            Logger logger = this.expansion.getLogger();
            logger.warning("Failed to save default config '" + jarName + "' because it does not exist in the jar.");
            return;
        }

        try {
            File parentFile = absoluteFile.getParentFile();
            if(parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
                Logger logger = this.expansion.getLogger();
                logger.warning("Failed to save default config '" + jarName + "' because the parent folder could not be created.");
                return;
            }

            if(!absoluteFile.createNewFile()) {
                Logger logger = this.expansion.getLogger();
                logger.warning("Failed to save default config '" + jarName + "' because the file could not be created.");
                return;
            }

            Path absolutePath = absoluteFile.toPath();
            Files.copy(jarStream, absolutePath, StandardCopyOption.REPLACE_EXISTING);
        } catch(IOException ex) {
            Logger logger = this.expansion.getLogger();
            logger.log(Level.WARNING,"Failed to save default config '" + jarName + "' because an I/O error occurred:", ex);
        }
    }
}