package com.SirBlobman.combatlogx;

import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import sun.net.www.protocol.http.HttpURLConnection;

public class NotUpdate {
	private final Not not;
	public NotUpdate(Not not) {this.not = not;}
	
	private static final String KEY = "98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4";
	private static final int ID = 42907;
	
	private String latest() {
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
			return not.getVersion();
		}
	}
	
	private void print(String... msg) {
		for(String s : msg) {
			String c = Util.color(s);
			ConsoleCommandSender ccs = Bukkit.getConsoleSender();
			ccs.sendMessage(c);
		}
	}
	
	public String check() {
		String latest = latest();
		String current = not.getVersion();
		if(latest.equals(current)) return "updated";
		else return latest;
	}
	
	public void print() {
		String v = check();
		if(v.equals("updated")) {
			String[] msg = new String[] {
				"&6========================================================",
				"&eNotCombatLogX Updater",
				"&aYou are using the latest version",
				"&6========================================================"
			};
			print(msg);
		} else {
			String[] msg = new String[] {
					"&6======================================================================================",
					"&eNotCombatLog Updater",
					"&aThere is a new update for this plugin!",
					"&4&lLatest Version: " + v,
					"&4&lYour Version: " + not.getVersion(),
					"&aGet it here: &4https://www.spigotmc.org/resources/notcombatlogx.42907/",
					"&6======================================================================================"
			};
			print(msg);
		}
	}
}