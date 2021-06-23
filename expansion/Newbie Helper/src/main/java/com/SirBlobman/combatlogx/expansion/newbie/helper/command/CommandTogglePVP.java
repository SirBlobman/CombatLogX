package com.SirBlobman.combatlogx.expansion.newbie.helper.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;
import com.SirBlobman.combatlogx.expansion.newbie.helper.NewbieHelper;
import com.SirBlobman.combatlogx.expansion.newbie.helper.listener.ListenerPVP;

public class CommandTogglePVP implements CommandExecutor {
    private final NewbieHelper expansion;
    private final ICombatLogX plugin;

    public CommandTogglePVP(NewbieHelper expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(checkNoPermission(sender, "combatlogx.command.pvptoggle")) return true;
        if(args.length < 1) return togglePVP(sender);
        if(args.length < 3) {
            String commandUsage = "/togglepvp {admin [player] [on/off]}";
            sender.sendMessage(commandUsage);
            return true;
        }

        String sub = args[0];
        if(!sub.equals("admin")) {
            String commandUsage = "/togglepvp {admin [player] [on/off]}";
            sender.sendMessage(commandUsage);
            return true;
        }

        if(checkNoPermission(sender, "combatlogx.command.pvptoggle.admin")) return true;
        ILanguageManager languageManager = this.plugin.getCombatLogXLanguageManager();

        Player target = getTarget(sender, args[1]);
        if(target == null) return true;
        String targetName = target.getName();

        String toggle = args[2].toLowerCase();
        if(toggle.equals("on")) {
            ListenerPVP listener = this.expansion.getPVPListener();
            listener.enablePVP(target);

            String message1 = languageManager.getMessageColoredWithPrefix("newbie-helper.pvptoggle.admin-enabled").replace("{target}", targetName);
            languageManager.sendMessage(sender, message1);

            String message2 = languageManager.getMessageColoredWithPrefix("newbie-helper.pvptoggle.enabled");
            languageManager.sendMessage(target, message2);
        } else if(toggle.equals("off")) {
            ListenerPVP listener = this.expansion.getPVPListener();
            listener.disablePVP(target);

            String message1 = languageManager.getMessageColoredWithPrefix("newbie-helper.pvptoggle.admin-disabled").replace("{target}", targetName);
            languageManager.sendMessage(sender, message1);

            String message2 = languageManager.getMessageColoredWithPrefix("newbie-helper.pvptoggle.disabled");
            languageManager.sendMessage(target, message2);
        } else {
            String commandUsage = "/togglepvp {admin [player] [on/off]}";
            sender.sendMessage(commandUsage);
            return true;
        }
        return true;
    }

    private boolean checkNoPermission(CommandSender sender, String permission) {
        if(sender.hasPermission(permission)) return false;
        ILanguageManager languageManager = this.plugin.getCombatLogXLanguageManager();

        String message = languageManager.getMessageColoredWithPrefix("errors.no-permission").replace("{permission}", permission);
        languageManager.sendMessage(sender, message);
        return true;
    }

    private Player getTarget(CommandSender sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if(target == null) {
            ILanguageManager languageManager = this.plugin.getCombatLogXLanguageManager();
            String message = languageManager.getMessageColoredWithPrefix("errors.invalid-target").replace("{target}", targetName);
            languageManager.sendMessage(sender, message);
            return null;
        }
        return target;
    }

    private boolean togglePVP(CommandSender sender) {
        ILanguageManager languageManager = this.plugin.getCombatLogXLanguageManager();
        if(!(sender instanceof Player)) {
            String message = languageManager.getMessageColoredWithPrefix("errors.not-player");
            languageManager.sendMessage(sender, message);
            return true;
        }
        Player player = (Player) sender;

        boolean pvpEnabled = this.expansion.getPVPListener().isPVPEnabled(player);
        ListenerPVP listener = this.expansion.getPVPListener();

        if(pvpEnabled) {
            listener.disablePVP(player);
            String message = languageManager.getMessageColoredWithPrefix("newbie-helper.pvptoggle.disabled");
            player.sendMessage(message);
        } else {
            listener.enablePVP(player);
            String message = languageManager.getMessageColoredWithPrefix("newbie-helper.pvptoggle.enabled");
            player.sendMessage(message);
        }

        return true;
    }
}
