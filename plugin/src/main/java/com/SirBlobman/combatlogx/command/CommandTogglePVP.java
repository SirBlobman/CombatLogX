package com.SirBlobman.combatlogx.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.api.command.Command;

public class CommandTogglePVP extends Command {
    public CommandTogglePVP(JavaPlugin plugin) {
        super(plugin, "togglepvp");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("This command requires the 'Newbie Helper' expansion.");
        sender.sendMessage("Please tell an admin to install it!");
        return true;
    }
}