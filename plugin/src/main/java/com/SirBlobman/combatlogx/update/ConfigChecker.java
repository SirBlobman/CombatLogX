package com.SirBlobman.combatlogx.update;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.CombatLogX;

import org.bukkit.configuration.file.FileConfiguration;

import org.zeroturnaround.zip.ZipUtil;

public final class ConfigChecker {
    private final CombatLogX plugin;
    public ConfigChecker(CombatLogX plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }
    
    public void checkConfig() {
        File pluginFolder = this.plugin.getDataFolder();
        File configFile = new File(pluginFolder, "config.yml");
        if(!configFile.exists()) return;
        
        FileConfiguration config = this.plugin.getConfig("config.yml");
        String generatedByVersion = config.getString("generated-by-version");
        if(generatedByVersion != null && generatedByVersion.startsWith("10")) return;
        
        Logger logger = this.plugin.getLogger();
        logger.warning("Detected an old CombatLogX config, a backup will be created and your config will be reset!");
        createBackup();
    }
    
    private void createBackup() {
        File pluginFolder = this.plugin.getDataFolder();
        Logger logger = this.plugin.getLogger();
        logger.info("Creating backup...");
    
        try {
            File pluginsFolder = pluginFolder.getParentFile();
            File backupFile = new File(pluginsFolder, "CombatLogX-backup-" + System.currentTimeMillis() + ".zip");
            ZipUtil.pack(pluginFolder, backupFile);
            deleteFiles(pluginFolder);
        } catch(Exception ex) {
            logger.log(Level.SEVERE, "An error occurred while creating a backup:", ex);
            logger.info("Failed to create backup.");
        }
    }
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteFiles(File parentFile) throws IOException {
        if(!parentFile.isDirectory()) {
            parentFile.delete();
            return;
        }
    
        File[] fileArray = parentFile.listFiles();
        if(fileArray == null || fileArray.length == 0) {
            parentFile.delete();
            return;
        }
        
        for(File file : fileArray) {
            String fileName = file.getName();
            if(fileName.endsWith(".jar")) continue;
            
            if(file.isDirectory()) {
                deleteFiles(file);
                continue;
            }
            
            file.delete();
        }
    
        File[] fileArray2 = parentFile.listFiles();
        if(fileArray2 == null || fileArray2.length == 0) parentFile.delete();
    }
}