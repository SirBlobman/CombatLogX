package com.SirBlobman.expansion.cheatprevention.olivolja3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.combatlogx.CombatLogX;


/**
 * -------------------------------------------------<br/>
 * 
 * This class was created for CombatLogXs expansion<br/>
 * CheatPrevention<br/><br/>
 * 
 * This class may be copied and modified to suit your<br/>
 * needs but you need to credit me (Olivo) for creating<br/>
 * this simple class.<br/><br/>
 * 
 * Minecraft: olivolja3<br/>
 * Discord: Olivo#3313<br/>
 * Youtube: OlivoCMD<br/>
 * 
 * -------------------------------------------------
 * 
 * @author olivolja3
 */
public class AliasDetection {
	
	private static List<String> aliases = new ArrayList<>();
	private static List<String> commands = new ArrayList<>();
	private static HashMap<String, String> aTC = new HashMap<>();
	private static Plugin plugin = JavaPlugin.getPlugin(CombatLogX.class);
	
	
	public static void cmdDetect() {
        for(Plugin plugin : plugin.getServer().getPluginManager().getPlugins()){
                List<Command> cmdList = PluginCommandYamlParser.parse(plugin);
                for(int i=0; i < cmdList.size(); i++){
                	Command cmd = cmdList.get(i);
					commands.add(cmd.getName());
					for(String alias : cmd.getAliases()) {
						aTC.put(alias, cmd.getName());
						aliases.add(alias);
					}
                }
        }
	}
	
	public static String aliasToCMD(String alias) {
		if(aTC.containsKey(alias)) {
			return aTC.get(alias);
		} else {
			return null;
		}
	}
	
	public static Boolean isAlias(String cmd) {
		if(!commands.contains(cmd)) return true;
		else return false;
	}
	
	public static List<String> getCommands() {
		return commands;
	}
	
	public static List<String> getAliases() {
		return aliases;
	}

}
