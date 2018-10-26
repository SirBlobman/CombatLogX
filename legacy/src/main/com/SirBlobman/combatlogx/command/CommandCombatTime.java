package com.SirBlobman.combatlogx.command;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandCombatTime implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
        if (cs instanceof Player) {
            Player p = (Player) cs;
            String cmd = c.getName().toLowerCase();
            if (cmd.equals("combattime")) {
                boolean in = Combat.isInCombat(p);
                if (in) {
                    long time = Combat.timeLeft(p);
                    List<String> l1 = Util.newList("{time_left}");
                    List<Object> l2 = Util.newList(time);
                    String msg = Util.formatMessage(ConfigLang.MESSAGE_STILL_IN_COMBAT, l1, l2);
                    Util.sendMessage(p, msg);
                    return true;
                } else {
                    String error = ConfigLang.MESSAGE_NOT_IN_COMBAT;
                    Util.sendMessage(p, error);
                    return true;
                }
            } else return false;
        } else {
            String error = ConfigLang.MESSAGE_NOT_PLAYER;
            Util.sendMessage(cs, error);
            return true;
        }
    }
}