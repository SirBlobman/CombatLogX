package com.SirBlobman.expansion.cheatprevention.olivolja3;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.SirBlobman.combatlogx.utility.Util;

import java.util.List;
import java.util.Map;


/**
 * -------------------------------------------------<br/>
 * <p>
 * This class was created for CombatLogX's expansion<br/>
 * CheatPrevention<br/><br/>
 * <p>
 * This class may be copied and modified to suit your<br/>
 * needs but you need to credit me (Olivo) for creating<br/>
 * this simple class.<br/><br/>
 * <p>
 * Minecraft: olivolja3<br/>
 * Discord: Olivo#3313<br/>
 * Youtube: OlivoCMD<br/>
 * <p>
 * -------------------------------------------------
 *
 * @author olivolja3
 */
public class AliasDetection {
    private static final List<String> ALIASES = Util.newList();
    private static final List<String> COMMANDS = Util.newList();
    private static final Map<String, String> ALIAS_TO_COMMAND = Util.newMap();

    public static void cmdDetect() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin[] pluginArray = manager.getPlugins();
        for(Plugin plugin : pluginArray) {
            List<Command> commandList = PluginCommandYamlParser.parse(plugin);
            for(Command command : commandList) {
                String cmd = command.getName();
                COMMANDS.add(cmd);
                
                List<String> aliasList = command.getAliases();
                for(String alias : aliasList) {
                    ALIAS_TO_COMMAND.put(alias, cmd);
                    ALIASES.add(alias);
                }
            }
        }
    }
}