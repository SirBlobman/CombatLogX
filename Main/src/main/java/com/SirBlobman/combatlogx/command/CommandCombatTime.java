package com.SirBlobman.combatlogx.command;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandCombatTime implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
        String cmd = c.getName().toLowerCase();
        if (cmd.equals("combattime")) {
            if (args.length > 0) {
                String target = args[0].toLowerCase();
                Player t = Bukkit.getPlayer(target);
                if (t != null) {
                    if (CombatUtil.isInCombat(t)) {
                        int time = CombatUtil.getTimeLeft(t);
                        List<String> keys = Util.newList("{target}", "{time}");
                        List<?> vals = Util.newList(t.getName(), time);
                        String format = ConfigLang.getWithPrefix("messages.commands.combattime.time left other");
                        String msg = Util.formatMessage(format, keys, vals);
                        cs.sendMessage(msg);
                        return true;
                    } else {
                        List<String> keys = Util.newList("{target}");
                        List<?> vals = Util.newList(t.getName());
                        String format = ConfigLang.getWithPrefix("messages.commands.combattime.not in combat other");
                        String error = Util.formatMessage(format, keys, vals);
                        Util.sendMessage(cs, error);
                        return true;
                    }
                } else {
                    List<String> keys = Util.newList("{target}");
                    List<?> vals = Util.newList(target);
                    String format = ConfigLang.getWithPrefix("messages.commands.invalid target");
                    String error = Util.formatMessage(format, keys, vals);
                    Util.sendMessage(cs, error);
                    return true;
                }
            } else {
                if (cs instanceof Player) {
                    Player p = (Player) cs;
                    if (CombatUtil.isInCombat(p)) {
                        int time = CombatUtil.getTimeLeft(p);
                        List<String> keys = Util.newList("{time}");
                        List<?> vals = Util.newList(time);
                        String format = ConfigLang.getWithPrefix("messages.commands.combattime.time left");
                        String msg = Util.formatMessage(format, keys, vals);
                        p.sendMessage(msg);
                        return true;
                    } else {
                        String error = ConfigLang.getWithPrefix("messages.commands.combattime.not in combat");
                        Util.sendMessage(cs, error);
                        return true;
                    }
                } else {
                    String error = ConfigLang.getWithPrefix("messages.commands.not player");
                    Util.sendMessage(cs, error);
                    return true;
                }
            }
        } else return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command c, String label, String[] args) {
        String cmd = c.getName().toLowerCase();
        if (cmd.equals("combattime")) {
            if (args.length == 1) return null;
            else return Util.newList();
        } else return null;
    }
}