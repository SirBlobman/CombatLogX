package com.SirBlobman.expansion.cheatprevention.olivolja3;

import com.SirBlobman.combatlogx.utility.Util;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * -------------------------------------------------<br/>
 * <p>
 * This class was created for CombatLogXs expansion<br/>
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
}