package com.SirBlobman.combatlogx.update;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.SirBlobman.combatlogx.CombatLogX;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitRunnable;

public final class UpdateChecker extends BukkitRunnable {
    private final CombatLogX plugin;
    private String spigotVersion, pluginVersion;
    private static final String SPIGOT_API_URL = "https://api.spigotmc.org/legacy/update.php?resource=31689";
    
    public UpdateChecker(CombatLogX plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");
        this.spigotVersion = null;
        this.pluginVersion = null;
    }
    
    @Override
    public void run() {
        String pluginVersion = getPluginVersion();
        String spigotVersion = getSpigotVersion();
        if(pluginVersion == null || spigotVersion == null) {
            printUpdateError();
            return;
        }
        
        String pluginVersionLower = pluginVersion.toLowerCase();
        if(pluginVersionLower.contains("beta")) {
            printBetaInfo();
            return;
        }
    
        VersionPart[] versionPartArray = VersionPart.values();
        for(VersionPart part : versionPartArray) {
            int spigotPart = getVersionPart(spigotVersion, part);
            int pluginPart = getVersionPart(pluginVersion, part);
            if(spigotPart == -1 || pluginPart == -1) {
                printUpdateError();
                return;
            }
            
            int response = checkVersions(spigotPart, pluginPart);
            if(response < 0) {
                printBetaInfo();
                return;
            }
            
            if(response > 0) {
                printUpdateInfo();
                return;
            }
            
            printLatestInfo();
        }
    }
    
    public void checkForUpdates() {
        FileConfiguration config = this.plugin.getConfig("config.yml");
        if(!config.getBoolean("update-checker")) return;
        runTaskAsynchronously(this.plugin);
    }
    
    public String getSpigotVersion() {
        if(this.spigotVersion != null) return this.spigotVersion;
        
        FileConfiguration config = this.plugin.getConfig("config.yml");
        if(!config.getBoolean("update-checker")) return (this.spigotVersion = "Update Checker Disabled!");
        
        print("Checking for updates using the Spigot API...");
        try {
            URL url = new URL(SPIGOT_API_URL);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("GET");
            
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            
            this.spigotVersion = bufferedReader.readLine();
            bufferedReader.close();
            return this.spigotVersion;
        } catch(Exception ex) {
            Logger logger = this.plugin.getLogger();
            logger.log(Level.WARNING, "An error occurred while trying to check for CombatLogX updates:", ex);
            return null;
        }
    }
    
    public String getPluginVersion() {
        if(this.pluginVersion != null) return this.pluginVersion;
    
        PluginDescriptionFile description = this.plugin.getDescription();
        return (this.pluginVersion = description.getVersion());
    }
    
    private int[] splitVersion(String version) {
        int[] failure = new int[4];
        Arrays.fill(failure, -1);
        if(version == null || version.isEmpty()) return failure;
        
        String[] split = version.split(Pattern.quote("."));
        if(split.length < 4) return failure;
        
        int[] splitVersion = new int[4];
        for(int i = 0; i < split.length; i++) {
            String partString = split[i];
            try {
                splitVersion[i] = Integer.parseInt(partString);
            } catch(NumberFormatException ex) {
                splitVersion[i] = -1;
            }
        }
        
        return splitVersion;
    }
    
    private int getVersionPart(String version, VersionPart part) {
        int[] splitVersion = splitVersion(version);
        return splitVersion[part.ordinal()];
    }
    
    private int checkVersions(int spigot, int plugin) {
        return Integer.compare(spigot, plugin);
    }
    
    private void printUpdateError() {
        print(
                "==============================================",
                "CombatLogX Update Checker",
                " ",
                "There was an error checking for updates",
                "=============================================="
        );
    }
    
    private void printUpdateInfo() {
        print(
                "==============================================",
                "CombatLogX Update Checker",
                " ",
                "There is an update available!",
                "Latest Version: " + this.spigotVersion,
                "Your Version: " + this.pluginVersion,
                " ",
                "https://www.spigotmc.org/resources/31689/",
                "&8=============================================="
        );
    }
    
    private void printBetaInfo() {
        print(
                "==============================================",
                "CombatLogX Update Checker",
                " ",
                "You are using a beta or bleeding-edge version.",
                "Thanks for testing CombatLogX!",
                "=============================================="
        );
    }
    
    private void printLatestInfo() {
        print(
            "==============================================",
            "CombatLogX Update Checker",
            " ",
            "You are using the latest version!",
            "=============================================="
        );
    }
    
    private void print(String... messageArray) {
        Logger logger = this.plugin.getLogger();
        Arrays.stream(messageArray).forEach(logger::info);
    }
    
    private enum VersionPart {
        MAJOR, MINOR, PATCH, EXPANSION
    }
}