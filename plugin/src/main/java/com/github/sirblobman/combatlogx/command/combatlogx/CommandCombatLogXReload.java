package com.github.sirblobman.combatlogx.command.combatlogx;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

public final class CommandCombatLogXReload extends CombatLogCommand {
    public CommandCombatLogXReload(ICombatLogX plugin) {
        super(plugin, "reload");
    }
    
    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if(!checkPermission(sender, "combatlogx.command.combatlogx.reload", true)) {
            return true;
        }
        
        ICombatLogX plugin = getCombatLogX();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        List<String> fileNameList = Arrays.asList("commands.yml", "config.yml", "language.yml", "punish.yml");
        for(String fileName : fileNameList) {
            configurationManager.reload(fileName);
        }
        
        LanguageManager languageManager = getLanguageManager();
        languageManager.reloadLanguages();
        
        ExpansionManager expansionManager = getExpansionManager();
        expansionManager.reloadConfigs();
        
        sendMessageWithPrefix(sender, "command.combatlogx.reload-success", null, true);
        return true;
    }
}
