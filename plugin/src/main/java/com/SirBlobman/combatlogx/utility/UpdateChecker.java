package com.SirBlobman.combatlogx.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.api.shaded.utility.MessageUtil;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitScheduler;

public final class UpdateChecker {
    private static final String SPIGOT_URL = "https://api.spigotmc.org/legacy/update.php?resource=31689";
    private static String spigotVersion = null;
    private static String pluginVersion = null;

    private static void print(String... messages) {
        CommandSender console = Bukkit.getConsoleSender();
        for(String message : messages) {
            message = MessageUtil.color(message);
            console.sendMessage(message);
        }
    }

    public static void checkForUpdates(CombatLogX plugin) {
        FileConfiguration config = plugin.getConfig("config.yml");
        if(!config.getBoolean("update-checker")) return;

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskAsynchronously(plugin, () -> updateTask(plugin));
    }

    private static void updateTask(CombatLogX plugin) {
        String spigotVersion = getSpigotVersion(plugin);
        String pluginVersion = getPluginVersion(plugin);
        if(pluginVersion.toLowerCase().contains("beta")) {
            print(
                    "&8==============================================",
                    "&eCombatLogX Update Checker",
                    " ",
                    "&cYou are using a beta or bleeding-edge version.",
                    "&cThanks for testing CombatLogX!",
                    "&8=============================================="
            );
            return;
        }

        for(VersionPart part : VersionPart.values()) {
            int spigotPart = getVersionPart(spigotVersion, part);
            int pluginPart = getVersionPart(pluginVersion, part);
            if(spigotPart == -1 || pluginPart == -1) {
                print(
                        "&8==============================================",
                        "&eCombatLogX Update Checker",
                        " ",
                        "&cThere was an error checking for updates",
                        "&8=============================================="
                );
                return;
            }

            int response = checkVersions(spigotPart, pluginPart);
            if(response < 0) {
                print(
                        "&8==============================================",
                        "&eCombatLogX Update Checker",
                        " ",
                        "&cYou are using a beta or bleeding-edge version.",
                        "&cThanks for testing CombatLogX!",
                        "&8=============================================="
                );
                return;
            }

            if(response > 0) {
                print(
                        "&8==============================================",
                        "&eCombatLogX Update Checker",
                        " ",
                        "&aThere is an update available!",
                        "&e&lLatest Version: &a" + spigotVersion,
                        "&e&lYour Version: &c" + pluginVersion,
                        "&eGet it here: &bhttps://www.spigotmc.org/resources/combatlogx.31689/",
                        "&8=============================================="
                );
                return;
            }
        }

        print(
                "&8==============================================",
                "&eCombatLogX Update Checker",
                " ",
                "&aYou are using the latest version!",
                "&8=============================================="
        );
    }

    public static String getSpigotVersion(CombatLogX plugin) {
        if(spigotVersion != null) return spigotVersion;

        FileConfiguration config = plugin.getConfig("config.yml");
        if(!config.getBoolean("update-checker")) return "Update Checker Disabled!";

        print("Checking for updates using the Spigot API...");
        try {
            URL url = new URL(SPIGOT_URL);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = (HttpURLConnection) urlConnection;
            connection.setRequestMethod("GET");

            InputStream stream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            spigotVersion = bufferedReader.readLine();
            bufferedReader.close();
            return spigotVersion;
        } catch(MalformedURLException ex) {
            print("Invalid Spigot URL for update checker, please contact SirBlobman!");
            ex.printStackTrace();
            return null;
        } catch(IOException ex) {
            print("An error occurred while trying to check for CombatLogX updates.");
            ex.printStackTrace();
            return null;
        }
    }

    public static String getPluginVersion(CombatLogX plugin) {
        if(pluginVersion != null) return pluginVersion;

        PluginDescriptionFile description = plugin.getDescription();
        String version = description.getVersion();
        if(version.contains("-")) {
            int indexOf = version.indexOf("-");
            version = version.substring(0, indexOf);
        }

        return pluginVersion = version;
    }

    private static int[] splitVersion(String version) {
        int[] failure = {-1, -1, -1, -1};
        if(version == null || version.isEmpty()) return failure;

        String[] split = version.split(Pattern.quote("."));
        if(split.length < 4) return failure;

        int[] splitVersion = new int[4];
        for(int i = 0; i < split.length; i++) {
            String string = split[i];
            try {
                int part = Integer.parseInt(string);
                splitVersion[i] = part;
            } catch(NumberFormatException ex) {splitVersion[i] = -1;}
        }
        return splitVersion;
    }

    private static int getVersionPart(String version, VersionPart part) {
        int arrayPart = part.getArrayPart();
        int[] splitVersion = splitVersion(version);
        return splitVersion[arrayPart];
    }

    private static int checkVersions(int spigot, int plugin) {
        return Integer.compare(spigot, plugin);
    }

    private enum VersionPart {
        MAJOR(0), MINOR(1), PATCH(2), EXPANSION(3);

        private final int arrayPart;
        VersionPart(int arrayPart) {
            this.arrayPart = arrayPart;
        }

        protected int getArrayPart() {
            return this.arrayPart;
        }
    }
}