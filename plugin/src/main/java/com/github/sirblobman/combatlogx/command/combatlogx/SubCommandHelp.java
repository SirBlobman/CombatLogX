package com.github.sirblobman.combatlogx.command.combatlogx;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;

public final class SubCommandHelp extends CombatLogCommand {
    public SubCommandHelp(ICombatLogX plugin) {
        super(plugin, "help");
        setPermissionName("combatlogx.command.combatlogx.help");
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        sendMessage(sender, "command.combatlogx.help-message-list", null);
        return true;
    }
}
