package com.SirBlobman.combatlog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import com.SirBlobman.combatlog.utility.Util;

import sun.net.www.protocol.http.HttpURLConnection;

public class Update {
	private static final String KEY = "98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4";
	private static final int ID = 31689;
	
	private static String latest() {
		try {
			URL url = new URL("http://www.spigotmc.org/api/general.php");
			URLConnection urlc = url.openConnection();
			HttpURLConnection hurlc = (HttpURLConnection) urlc;
			hurlc.setDoOutput(true);
			hurlc.setRequestMethod("POST");
			
			OutputStream os = hurlc.getOutputStream();
			String req = "key=" + KEY + "&resource=" + ID;
			byte[] write = req.getBytes("UTF-8");
			os.write(write);
			
			InputStream is = hurlc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String version = br.readLine();
			
			is.close();
			os.close();
			return version;
		} catch(Throwable ex) {
			String error = "Failed to check for update:\n" + ex.getCause();
			Util.print(error);
			return current();
		}
	}
	
	private static String current() {
		CombatLog CL = CombatLog.instance;
		PluginDescriptionFile pdf = CL.getDescription();
		String version = pdf.getVersion();
		return version;
	}
	
	private static void print(String... msg) {
		for(String s : msg) {
			String c = Util.color(s);
			ConsoleCommandSender ccs = Bukkit.getConsoleSender();
			ccs.sendMessage(c);
		}
	}
	
	public static String check() {
		String latest = latest();
		String current = current();
		if(latest.equals(current)) return "updated";
		else return latest;
	}
	
	public static void print() {
		String v = check();
		if(v.equals("updated")) {
			String[] msg = new String[] {
				"&6========================================================",
				"&eCombatLog Updater",
				"&aYou are using the latest version",
				"&6========================================================"
			};
			print(msg);
		} else {
			String[] msg = new String[] {
					"&6======================================================================================",
					"&eCombatLog Updater",
					"&aThere is a new update for this plugin!",
					"&4&lLatest Version: " + v,
					"&4&lYour Version: " + current(),
					"&aGet it here: &4https://www.spigotmc.org/resources/combatlog.31689/",
					"&6======================================================================================"
			};
			print(msg);
		}
	}
}