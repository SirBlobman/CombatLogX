package com.SirBlobman.expansion.cheatprevention.olivolja3;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;

import com.SirBlobman.combatlogx.utility.Util;


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
    private static List<String> aliases = Util.newList();
    private static List<String> commands = Util.newList();
    private static Map<String, String> aliasToCommand = Util.newMap();
    
    public static void cmdDetect() {
        Arrays.stream(Util.PM.getPlugins()).forEach(plugin -> {
            List<Command> cmdList = PluginCommandYamlParser.parse(plugin);
            cmdList.forEach(cmd -> {
                String name = cmd.getName();
                commands.add(name);
                cmd.getAliases().forEach(alias -> {
                    aliasToCommand.put(alias, name);
                    aliases.add(alias);
                });
            });
        });
    }
    
    public static String aliasToCommand(String alias) {return aliasToCommand.getOrDefault(alias, null);}
    public static boolean isAlias(String cmd) {return aliases.contains(cmd);}
    public static List<String> getCommands() {return commands;}
    public static List<String> getAliases() {return aliases;}
}