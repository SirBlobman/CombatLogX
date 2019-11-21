package com.SirBlobman.combatlogx.expansion.newbie.helper.command;

import com.SirBlobman.combatlogx.expansion.newbie.helper.NewbieHelper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandTogglePVP implements CommandExecutor {
    private final NewbieHelper expansion;
    public CommandTogglePVP(NewbieHelper expansion) {
        this.expansion = expansion;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}