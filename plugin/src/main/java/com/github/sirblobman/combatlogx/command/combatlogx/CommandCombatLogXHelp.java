package com.github.sirblobman.combatlogx.command.combatlogx;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;

public final class CommandCombatLogXHelp extends CombatLogCommand {
    public CommandCombatLogXHelp(ICombatLogX plugin) {
        super(plugin, "help");
    }
    
    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if(!checkPermission(sender, "combatlogx.command.combatlogx.help", true)) {
            return true;
        }
        
        sendMessage(sender, "command.combatlogx.help-message-list", null, true);
        return true;
    }
}
