package com.SirBlobman.combatlogx.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagReason;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.UpdateUtil;
import com.SirBlobman.combatlogx.utility.Util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class CommandCombatLogX implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        if(!cmd.equals("combatlogx")) return false;
        if(args.length < 1) return false;
        
        String sub = args[0].toLowerCase();
        switch(sub) {
        case "reload": return reload(sender);
        case "tag": return tag(sender, args);
        case "untag": return untag(sender, args);
        case "version": return version(sender);
        case "toggle": return toggle(sender, args);
        
        default: return false;
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender cs, Command c, String label, String[] args) {
        String cmd = c.getName().toLowerCase();
        if(!cmd.equals("combatlogx")) return Util.newList();
        
        if(args.length == 1) {
            String sub = args[0];
            List<String> valid = Util.newList("reload", "tag", "untag", "version", "toggle");
            return Util.getMatching(valid, sub);
        }
        
        if(args.length == 2) {
            String sub = args[0].toLowerCase();
            if(sub.equals("toggle")) {
                List<String> valid = Util.newList("bossbar", "actionbar", "scoreboard");
                return Util.getMatching(valid, sub);
            }
            
            if(sub.equals("tag") || sub.equals("untag")) return null;
        }
        
        return Util.newList();
    }
    
    private boolean toggle(CommandSender sender, String[] args) {
        String permission = "combatlogx.notifier.toggle";
        if(!sender.hasPermission(permission)) {
            List<String> keys = Util.newList("{permission}");
            List<String> vals = Util.newList(permission);
            String format = ConfigLang.getWithPrefix("messages.commands.no permission");
            String error = Util.formatMessage(format, keys, vals);
            Util.sendMessage(sender, error);
            return true;
        }
        
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can toggle stuff.");
            return true;
        }
        
        if(!Expansions.isEnabled("Notifier")) {
            sender.sendMessage("Notifier is not installed!");
            return true;
        }
        
        if(args.length < 2) return false;
        
        Player player = (Player) sender;
        String toggleType = args[1].toLowerCase();
        if(toggleType.equals("scoreboard")) {
            try {
                Class<?> class_ScoreboardUtil = Class.forName("com.SirBlobman.expansion.notifier.utility.ScoreboardUtil");
                Method method_toggle = class_ScoreboardUtil.getMethod("toggle", Player.class);
                boolean on = (boolean) method_toggle.invoke(null, player);
                sender.sendMessage("Score Board: " + (on ? "ON" : "OFF"));
                return true;
            } catch(ClassNotFoundException ex) {
                sender.sendMessage("Notifier is not installed!");
                return true;
            } catch(NoSuchMethodException | InvocationTargetException | IllegalArgumentException | IllegalAccessException ex) {
                sender.sendMessage("Failed to toggle score board, tell an admin to check the console :(");
                ex.printStackTrace();
                return true;
            }
        }
        
        if(toggleType.equals("bossbar")) {
            try {
                Class<?> class_ScoreboardUtil = Class.forName("com.SirBlobman.expansion.notifier.utility.BossBarUtil");
                Method method_toggle = class_ScoreboardUtil.getMethod("toggle", Player.class);
                boolean on = (boolean) method_toggle.invoke(null, player);
                sender.sendMessage("Boss Bar: " + (on ? "ON" : "OFF"));
                return true;
            } catch(ClassNotFoundException ex) {
                sender.sendMessage("Notifier is not installed!");
                return true;
            } catch(NoSuchMethodException | InvocationTargetException | IllegalArgumentException | IllegalAccessException ex) {
                sender.sendMessage("Failed to toggle boss bar, tell an admin to check the console :(");
                ex.printStackTrace();
                return true;
            }
        }
        
        if(toggleType.equals("actionbar")) {
            try {
                Class<?> class_ScoreboardUtil = Class.forName("com.SirBlobman.expansion.notifier.utility.ActionBarUtil");
                Method method_toggle = class_ScoreboardUtil.getMethod("toggle", Player.class);
                boolean on = (boolean) method_toggle.invoke(null, player);
                sender.sendMessage("Action Bar: " + (on ? "ON" : "OFF"));
                return true;
            } catch(ClassNotFoundException ex) {
                sender.sendMessage("Notifier is not installed!");
                return true;
            } catch(NoSuchMethodException | InvocationTargetException | IllegalArgumentException | IllegalAccessException ex) {
                sender.sendMessage("Failed to toggle action bar, tell an admin to check the console :(");
                ex.printStackTrace();
                return true;
            }
        }
        
        return false;
    }
    
    private boolean reload(CommandSender cs) {
        String perm = "combatlogx.reload";
        if (cs.hasPermission(perm)) {
            ConfigOptions.load();
            ConfigLang.load();
            Expansions.reloadConfigs();
            
            String msg = ConfigLang.getWithPrefix("messages.commands.combatlogx.reloaded");
            Util.sendMessage(cs, msg);
            return true;
        } else {
            List<String> keys = Util.newList("{permission}");
            List<String> vals = Util.newList(perm);
            String format = ConfigLang.getWithPrefix("messages.commands.no permission");
            String error = Util.formatMessage(format, keys, vals);
            Util.sendMessage(cs, error);
            return true;
        }
    }
    
    private boolean tag(CommandSender cs, String[] args) {
        String perm = "combatlogx.tag";
        if (cs.hasPermission(perm)) {
            if (args.length > 1) {
                String targetName = args[1];
                Player target = Bukkit.getPlayer(targetName);
                if (target != null) {
                    CombatUtil.tag(target, null, TagType.UNKNOWN, TagReason.UNKNOWN);
                    List<String> keys = Util.newList("{target}");
                    List<?> vals = Util.newList(target.getName());
                    String format = ConfigLang.getWithPrefix("messages.commands.combatlogx.tag");
                    String msg = Util.formatMessage(format, keys, vals);
                    Util.sendMessage(cs, msg);
                    return true;
                } else {
                    List<String> keys = Util.newList("{target}");
                    List<?> vals = Util.newList(targetName);
                    String format = ConfigLang.getWithPrefix("messages.commands.invalid target");
                    String error = Util.formatMessage(format, keys, vals);
                    Util.sendMessage(cs, error);
                    return true;
                }
            } else return false;
        } else {
            List<String> keys = Util.newList("{permission}");
            List<String> vals = Util.newList(perm);
            String format = ConfigLang.getWithPrefix("messages.commands.no permission");
            String error = Util.formatMessage(format, keys, vals);
            Util.sendMessage(cs, error);
            return true;
        }
    }
    
    private boolean untag(CommandSender cs, String[] args) {
        String perm = "combatlogx.untag";
        if (cs.hasPermission(perm)) {
            if (args.length > 1) {
                String target = args[1];
                Player t = Bukkit.getPlayer(target);
                if (t != null) {
                    if (CombatUtil.isInCombat(t)) {
                        CombatUtil.untag(t, UntagReason.EXPIRE);
                        List<String> keys = Util.newList("{target}");
                        List<?> vals = Util.newList(t.getName());
                        String format = ConfigLang.getWithPrefix("messages.commands.combatlogx.untag");
                        String msg = Util.formatMessage(format, keys, vals);
                        Util.sendMessage(cs, msg);
                        return true;
                    } else {
                        String error = ConfigLang.getWithPrefix("messages.commands.combatlogx.not in combat");
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
            } else return false;
        } else {
            List<String> keys = Util.newList("{permission}");
            List<String> vals = Util.newList(perm);
            String format = ConfigLang.getWithPrefix("messages.commands.no permission");
            String error = Util.formatMessage(format, keys, vals);
            Util.sendMessage(cs, error);
            return true;
        }
    }
    
    private boolean version(CommandSender sender) {
        String perm = "combatlogx.version";
        if(!sender.hasPermission(perm)) {
            List<String> keys = Util.newList("{permission}");
            List<String> vals = Util.newList(perm);
            String format = ConfigLang.getWithPrefix("messages.commands.no permission");
            String error = Util.formatMessage(format, keys, vals);
            Util.sendMessage(sender, error);
            return true;
        }
        
        Util.sendMessage(sender, "Getting version information...");
        SchedulerUtil.runNowAsync(() -> {
            String pversion = UpdateUtil.getPluginVersion();
            String sversion = UpdateUtil.getSpigotVersion();
            
            String[] msg = Util.color(
                    " ",
                    "&f&lFull Version: &7" + Bukkit.getVersion(),
                    "&f&lBukkit Version: &7" + Bukkit.getBukkitVersion(),
                    "&f&lMinecraft Version: &7" + NMS_Handler.getMinecraftVersion(),
                    "&f&lNMS Version: &7" + NMS_Handler.getNetMinecraftServerVersion(),
                    " ", 
                    "&f&lCombatLogX by SirBlobman",
                    " ",
                    "&f&lLatest Version: &7v" + sversion,
                    "&f&lInstalled Version: &7v" + pversion,
                    " ",
                    "&7&oGetting expansion versions...",
                    " "
                    );
            sender.sendMessage(Util.color(msg));
            
            List<CLXExpansion> expansions = Expansions.getExpansions();
            if (expansions.isEmpty()) {
                String error = Util.color("  &f&lYou do not have any expansions.");
                Util.sendMessage(sender, error);
            } else expansions.forEach(clxe -> {
                String name = clxe.getName();
                String version = clxe.getVersion();
                String msg1 = Util.color("  &f&l" + name + " &7v" + version);
                Util.sendMessage(sender, msg1);
            });
        });
        return true;
    }
}