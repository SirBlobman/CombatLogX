package com.SirBlobman.combatlogx.command;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.command.*;

public class CommandConfig implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
        String cmd = c.getName().toLowerCase();
        if(cmd.equals("clxconfig")) {
            Config.loadC();
            Config.loadL();
            String msg = Config.MESSAGE_RELOAD_CONFIG;
            Util.sendMessage(cs, msg);
            return true;
        } else return false;
    }
}