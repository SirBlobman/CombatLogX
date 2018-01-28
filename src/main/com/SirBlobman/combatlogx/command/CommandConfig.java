package com.SirBlobman.combatlogx.command;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandConfig implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
        String cmd = c.getName().toLowerCase();
        if(cmd.equals("clxconfig")) {
            ConfigOptions.load();
            ConfigLang.load();
            String msg = ConfigLang.MESSAGE_RELOAD_CONFIG;
            Util.sendMessage(cs, msg);
            return true;
        } else return false;
    }
}