package com.github.sirblobman.combatlogx.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;

public final class CommandTogglePVP extends CombatLogCommand {
    public CommandTogglePVP(ICombatLogX plugin) {
        super(plugin, "togglepvp");
        setPermissionName("combatlogx.command.togglepvp");
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("This command requires the 'Newbie Helper' expansion.");
        sender.sendMessage("Please tell an admin to install it!");
        return true;
    }
}
