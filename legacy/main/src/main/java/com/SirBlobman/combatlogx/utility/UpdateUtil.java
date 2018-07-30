package com.SirBlobman.combatlogx.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UpdateUtil extends Util {
	private static final String SPIGOT_URL = "https://api.spigotmc.org/legacy/update.php?resource=31689";
	private static String spigotVersion = null;

	public static void checkForUpdates() {
		BS.runTaskAsynchronously(PLUGIN, () -> {
			String spigotVersion = getSpigotVersion();
			String[] error = Util.color(
				"You are using a legacy version for 1.8.8 - 1.12.2",
				"The latest version is for 1.13 and is '" + spigotVersion + "'."
			);
			print(error);
		});
	}

	public static String[] getErrorMessage() {
		String[] error = color(
			"&8==============================================",
			"&eCombatLogX Update Checker",
			" ",
			"&cThere was an error checking for updates",
			"&8=============================================="
		);
		return error;
	}

	public static String getSpigotVersion() {
		if(spigotVersion != null) return spigotVersion;
		else {
			try {
				print("Checking for updates using Spigot API...");
				URL url = new URL(SPIGOT_URL);
				URLConnection urlc = url.openConnection();
				HttpURLConnection http = (HttpURLConnection) urlc;
				http.setRequestMethod("GET");

				InputStream is = http.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String version = br.readLine();
				br.close();
				isr.close();
				is.close();
				spigotVersion = version;
				return version;
			} catch (Throwable ex) {return null;}
		}
	}
}