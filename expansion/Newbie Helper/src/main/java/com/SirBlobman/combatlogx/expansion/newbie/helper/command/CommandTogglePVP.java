package com.SirBlobman.combatlogx.expansion.newbie.helper.command;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.expansion.newbie.helper.NewbieHelper;
import com.SirBlobman.combatlogx.expansion.newbie.helper.listener.ListenerPVP;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTogglePVP implements CommandExecutor {
    private final NewbieHelper expansion;
    private final ICombatLogX plugin;
    private final String commandUsage = "/togglepvp {admin [player] [on/off]}";
    public CommandTogglePVP(NewbieHelper expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(checkNoPermission(sender, "combatlogx.command.pvptoggle")) return true;

        if(args.length < 1) return togglePVP(sender);
        if(args.length < 3) {
            sender.sendMessage(this.commandUsage);
            return true;
        }

        String sub = args[0];
        if(!sub.equals("admin")) {
            sender.sendMessage(this.commandUsage);
            return true;
        }

        if(checkNoPermission(sender, "combatlogx.command.pvptoggle.admin")) return true;

        Player target = getTarget(sender, args[1]);
        if(target == null) return true;
        String targetName = target.getName();

        String toggle = args[2].toLowerCase();
        if(toggle.equals("on")) {
            ListenerPVP listener = this.expansion.getPVPListener();
            listener.enablePVP(target);

            String message1 = this.plugin.getLanguageMessageColoredWithPrefix("newbie-helper.pvptoggle.admin-enabled").replace("{target}", targetName);
            sender.sendMessage(message1);

            String message2 = this.plugin.getLanguageMessageColoredWithPrefix("newbie-helper.pvptoggle.enabled");
            target.sendMessage(message2);
        } else if(toggle.equals("off")) {
            ListenerPVP listener = this.expansion.getPVPListener();
            listener.disablePVP(target);

            String message1 = this.plugin.getLanguageMessageColoredWithPrefix("newbie-helper.pvptoggle.admin-disabled").replace("{target}", targetName);
            sender.sendMessage(message1);

            String message2 = this.plugin.getLanguageMessageColoredWithPrefix("newbie-helper.pvptoggle.disabled");
            target.sendMessage(message2);
        } else {
            sender.sendMessage(this.commandUsage);
            return true;
        }
        return true;
    }

    private boolean checkNoPermission(CommandSender sender, String permission) {
        if(sender.hasPermission(permission)) return false;

        String message = this.plugin.getLanguageMessageColoredWithPrefix("errors.no-permission").replace("{permission}", permission);
        this.plugin.sendMessage(sender, message);
        return true;
    }

    private Player getTarget(CommandSender sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if(target == null) {
            String message = this.plugin.getLanguageMessageColoredWithPrefix("errors.invalid-target").replace("{target}", targetName);
            this.plugin.sendMessage(sender, message);
            return null;
        }
        return target;
    }

    private boolean togglePVP(CommandSender sender) {
        if(!(sender instanceof Player)) {
            String message = this.plugin.getLanguageMessageColoredWithPrefix("errors.not-player");
            this.plugin.sendMessage(sender, message);
            return true;
        }
        Player player = (Player) sender;

        boolean pvpEnabled = this.expansion.getPVPListener().isPVPEnabled(player);
        if(pvpEnabled) {
            ListenerPVP listener = this.expansion.getPVPListener();
            listener.disablePVP(player);

            String message = this.plugin.getLanguageMessageColoredWithPrefix("newbie-helper.pvptoggle.disabled");
            player.sendMessage(message);
        } else {
            ListenerPVP listener = this.expansion.getPVPListener();
            listener.enablePVP(player);

            String message = this.plugin.getLanguageMessageColoredWithPrefix("newbie-helper.pvptoggle.enabled");
            player.sendMessage(message);
        }
        return true;
    }
}