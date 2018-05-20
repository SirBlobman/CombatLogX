package com.SirBlobman.combatlogx.utility;

import org.bukkit.plugin.PluginDescriptionFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UpdateUtil extends Util {
    private static final String SPIGOT_URL = "https://api.spigotmc.org/legacy/update.php?resource=31689";
    
    public static void checkForUpdates() {
        BS.runTaskAsynchronously(PLUGIN, new Runnable() {
           @Override
           public void run() {
               String spigot = getSpigotVersion();
               String plugin = getPluginVersion();
               if(spigot == null || plugin == null) {
                   printNoPrefix(
                           "&6==================================================================",
                           "&eCombatLogX Update Checker",
                           "&cCould not connect to the Spigot Update API",
                           "&6=================================================================="
                   );
               } else if(plugin.equals(spigot)) {
                   printNoPrefix(
                       "&6==================================================================",
                       "&eCombatLogX Update Checker",
                       "&aYou are using the latest version!",
                       "&6=================================================================="
                   );
               } else {
                   printNoPrefix(
                       "&6==================================================================",
                       "&eCombatLogX Update Checker",
                       "&aThere may be an update available!",
                       "&e&lLatest Version: &a" + spigot,
                       "&e&lYour Version: &c" + plugin,
                       "&eGet it here: &bhttps://www.spigotmc.org/resources/combatlogx.31689/",
                       "&6=================================================================="
                   );
               }
           }
        });
    }

    public static String getSpigotVersion() {
        try {
            URL url = new URL(SPIGOT_URL);
            URLConnection urlc = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) urlc;
            http.setRequestMethod("GET");
            
            InputStream is = http.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String version = br.readLine();
            br.close(); isr.close(); is.close();
            return version;
        } catch(Throwable ex) {return null;}
    }
    
    public static String getPluginVersion() {
        PluginDescriptionFile pdf = PLUGIN.getDescription();
        String version = pdf.getVersion();
        return version;
    }
}