package com.SirBlobman.combatlogx.command;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCombatTimer implements CommandExecutor {
    private final ICombatLogX plugin;
    public CommandCombatTimer(ICombatLogX plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) {
            if(!(sender instanceof Player)) {
                String message = this.plugin.getLanguageMessageColoredWithPrefix("errors.not-player");
                this.plugin.sendMessage(sender, message);
                return true;
            }

            Player player = (Player) sender;
            checkSelf(player);
            return true;
        }

        String targetName = args[0];
        Player target = getTarget(sender, targetName);
        if(target == null) return true;

        checkOther(sender, target);
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

    private void checkSelf(Player player) {
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) {
            String message = this.plugin.getLanguageMessageColoredWithPrefix("errors.self-not-in-combat");
            this.plugin.sendMessage(player, message);
            return;
        }

        int timeLeft = combatManager.getTimerSecondsLeft(player);
        String timeLeftString = Integer.toString(timeLeft);

        String message = this.plugin.getLanguageMessageColoredWithPrefix("commands.combattimer.time-left.self").replace("{time}", timeLeftString);
        this.plugin.sendMessage(player, message);
    }

    private void checkOther(CommandSender sender, Player target) {
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(target)) {
            String message = this.plugin.getLanguageMessageColoredWithPrefix("errors.target-not-in-combat").replace("{target}", target.getName());
            this.plugin.sendMessage(sender, message);
            return;
        }

        int timeLeft = combatManager.getTimerSecondsLeft(target);
        String timeLeftString = Integer.toString(timeLeft);

        String message = this.plugin.getLanguageMessageColoredWithPrefix("commands.combattimer.time-left.other").replace("{time}", timeLeftString).replace("{target}", target.getName());
        this.plugin.sendMessage(sender, message);
    }
}
