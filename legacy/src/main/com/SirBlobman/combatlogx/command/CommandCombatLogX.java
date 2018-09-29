package com.SirBlobman.combatlogx.command;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagCause;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.Util;

public class CommandCombatLogX implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
        String cmd = c.getName().toLowerCase();
        if (cmd.equals("combatlogx")) {
            if (args.length > 0) {
                String sub = args[0].toLowerCase();
                if (sub.equals("reload")) {
                    String perm = "combatlogx.reload";
                    if (cs.hasPermission(perm)) {
                        ConfigOptions.load();
                        ConfigLang.load();
                        String msg = ConfigLang.MESSAGE_RELOAD_CONFIG;
                        Util.sendMessage(cs, msg);
                        return true;
                    } else {
                        String error = ConfigLang.MESSAGE_NO_PERMISSION;
                        Util.sendMessage(cs, error);
                        return true;
                    }
                } else if (sub.equals("tag")) {
                    if (args.length > 1) {
                        String perm = "combatlogx.tag";
                        if (cs.hasPermission(perm)) {
                            String target = args[1];
                            Player t = Bukkit.getPlayer(target);
                            if (t != null) {
                                Combat.tag(t, null);
                                if (Expansions.isEnabled("NotCombatLogX")) {
                                    try {
                                        Class<?> clazz = Class.forName("com.SirBlobman.not.NotCombatLogX");
                                        CLXExpansion ex = Expansions.getByName("NotCombatLogX");
                                        Method m = clazz.getMethod("call", Player.class);
                                        m.invoke(ex, t);
                                    } catch (Throwable ex) {
                                        String error = "There was an error trying to tag '" + t.getName() + "'";
                                        Util.print(error);
                                        ex.printStackTrace();
                                    }
                                }
                                String msg = Util.formatMessage(ConfigLang.MESSAGE_FORCE_TAG, Util.newList("{target}"),
                                        Util.newList(t.getName()));
                                cs.sendMessage(msg);
                                return true;
                            } else {
                                String error = Util.formatMessage(ConfigLang.MESSAGE_INVALID_TARGET,
                                        Util.newList("{target}"), Util.newList(target));
                                Util.sendMessage(cs, error);
                                return true;
                            }
                        } else {
                            String error = ConfigLang.MESSAGE_NO_PERMISSION;
                            Util.sendMessage(cs, error);
                            return true;
                        }
                    } else
                        return false;
                } else if (sub.equals("untag")) {
                    if (args.length > 1) {
                        String perm = "combatlogx.untag";
                        if (cs.hasPermission(perm)) {
                            String target = args[1];
                            Player t = Bukkit.getPlayer(target);
                            if (t != null) {
                                Combat.remove(t);
                                PlayerUntagEvent pue = new PlayerUntagEvent(t, UntagCause.EXPIRE);
                                Util.call(pue);
                                String msg = Util.formatMessage(ConfigLang.MESSAGE_FORCE_UNTAG,
                                        Util.newList("{target}"), Util.newList(t.getName()));
                                cs.sendMessage(msg);
                                return true;
                            } else {
                                String error = Util.formatMessage(ConfigLang.MESSAGE_INVALID_TARGET,
                                        Util.newList("{target}"), Util.newList(target));
                                Util.sendMessage(cs, error);
                                return true;
                            }
                        } else {
                            String error = ConfigLang.MESSAGE_NO_PERMISSION;
                            Util.sendMessage(cs, error);
                            return true;
                        }
                    } else return false;
                } else if(sub.equals("version")) {
                    PluginDescriptionFile pdf = CombatLogX.INSTANCE.getDescription();
                    String clxVersion = pdf.getVersion();
                    Util.sendInfoMessage(cs, "&fCombatLogX &7v" + clxVersion);
                    Util.sendInfoMessage(cs, "&l");
                    Util.sendInfoMessage(cs, "&f&lExpansions:");
                    
                    for(CLXExpansion exp : Expansions.getExpansions()) {
                        String name = exp.getName();
                        String version = exp.getVersion();
                        String app = "&7 - &f" + name + " &7v" + version;
                        Util.sendInfoMessage(cs, app);
                    }
                    return true;
                } else return false;
            } else return false;
        } else return false;
    }
}