package com.SirBlobman.combat_log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class Update
{
	private static String prefix = Config.option("messages.prefix");
	private static String key = "98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4";
	private static int resource = 31689;
	
	private static String latestVersion()
	{
		try
		{
			URL url = new URL("http://www.spigotmc.org/api/general.php");
			URLConnection uc = url.openConnection();
			HttpURLConnection con = (HttpURLConnection) uc;
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.getOutputStream().write(("key=" + key + "&resource=" + resource).getBytes("UTF-8"));
			InputStream is = con.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String version = br.readLine();
			if(version.length() <= 7) return version;
			return "";
		} catch(Exception ex)
		{
			System.out.println(prefix + "Failed to check for an update: " + ex.getMessage());
			return "";
		}
	}
	
	private static String currentVersion()
	{
		CombatLog CL = CombatLog.instance;
		PluginDescriptionFile pdf = CL.getDescription();
		String version = pdf.getVersion();
		return version;
	}
	
	private static String check()
	{
		String latest = latestVersion();
		String current = currentVersion();
		if(latest.equals(current)) return "updated";
		else return latest;
	}
	
	public static void print()
	{
		String v = check();
		if(v == "updated")
		{
			String[] msg = new String[] 
			{
				"&6========================================================",
				"&eCombatLog Updater",
				"&aYou are using the latest version",
				"&6========================================================"
			};
			print(msg);
		}
		else
		{
			String[] msg = new String[] 
			{
				"&6======================================================================================",
				"&eCombatLog Updater",
				"&aThere is a new update for this plugin!",
				"&4&lLatest Version: " + v,
				"&4&lYour Version: " + currentVersion(),
				"&aGet it here: &4https://www.spigotmc.org/resources/combatlog.31689/",
				"&6======================================================================================"
			};
			print(msg);
		}
	}
	
	private static void print(String... msg)
	{
		for(String s : msg) 
		{
			String c = ChatColor.translateAlternateColorCodes('&', s);
			ConsoleCommandSender ccs = Bukkit.getConsoleSender();
			ccs.sendMessage(c);
		}
	}
}