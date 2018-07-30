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
                switch (sub) {
                    case "reload":
                        if (cs.hasPermission("combatlogx.reload\"")) {
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
                    case "tag":
                        if (args.length > 1) {
                            if (cs.hasPermission("combatlogx.tag")) {
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
                    case "untag":
                        if (args.length > 1) {
                            if (cs.hasPermission("combatlogx.untag")) {
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
                    case "version":
                        PluginDescriptionFile pdf = CombatLogX.INSTANCE.getDescription();
                        String clxVersion = pdf.getVersion();
                        Util.sendInfoMessage(cs, "&fCombatLogX &7v" + clxVersion);
                        Util.sendInfoMessage(cs, "&l");
                        Util.sendInfoMessage(cs, "&f&lExpansions:");

                        Expansions.getExpansions().forEach(expansion -> {
                            String app = "&7 - &f" + expansion.getName() + " &7v" + expansion.getVersion();

                            Util.sendInfoMessage(cs, app);
                        });
                        return true;
                    default:
                        return false;
                }
            } else return false;
        } else return false;
    }
}