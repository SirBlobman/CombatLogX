package com.SirBlobman.combatlogx.command;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandConfig implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
        if(cs instanceof Player) {
            String cmd = c.getName().toLowerCase();
            if(cmd.equals("clxconfig")) {
                Player p = (Player) cs;
                Config.loadC();
                Config.loadL();
                String msg = Config.MESSAGE_RELOAD_CONFIG;
                Util.sendMessage(p, msg);
                return true;
            } else return false;
        } else {
            String error = Config.MESSAGE_NOT_PLAYER;
            Util.sendMessage(cs, error);
            return true;
        }
    }
}