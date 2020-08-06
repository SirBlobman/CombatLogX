package com.SirBlobman.combatlogx.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;

public class CommandCombatTimer implements CommandExecutor {
    private final ICombatLogX plugin;
    public CommandCombatTimer(ICombatLogX plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) {
            if(!(sender instanceof Player)) {
                ILanguageManager languageManager = this.plugin.getLanguageManager();
                String message = languageManager.getMessageColoredWithPrefix("errors.not-player");
                languageManager.sendMessage(sender, message);
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
            ILanguageManager languageManager = this.plugin.getLanguageManager();
            String message = languageManager.getMessageColoredWithPrefix("errors.invalid-target").replace("{target}", targetName);
            languageManager.sendMessage(sender, message);
            return null;
        }
        
        return target;
    }

    private void checkSelf(Player player) {
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) {
            ILanguageManager languageManager = this.plugin.getLanguageManager();
            String message = languageManager.getMessageColoredWithPrefix("errors.self-not-in-combat");
            languageManager.sendMessage(player, message);
            return;
        }

        int timeLeft = combatManager.getTimerSecondsLeft(player);
        String timeLeftString = Integer.toString(timeLeft);
        
        ILanguageManager languageManager = this.plugin.getLanguageManager();
        String message = languageManager.getMessageColoredWithPrefix("commands.combattimer.time-left-self").replace("{time}", timeLeftString);
        languageManager.sendMessage(player, message);
    }

    private void checkOther(CommandSender sender, Player target) {
        String targetName = target.getName();
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(target)) {
            ILanguageManager languageManager = this.plugin.getLanguageManager();
            String message = languageManager.getMessageColoredWithPrefix("errors.target-not-in-combat").replace("{target}", targetName);
            languageManager.sendMessage(sender, message);
            return;
        }

        int timeLeft = combatManager.getTimerSecondsLeft(target);
        String timeLeftString = Integer.toString(timeLeft);
        
        ILanguageManager languageManager = this.plugin.getLanguageManager();
        String message = languageManager.getMessageColoredWithPrefix("errors.combattimer-time-left-other").replace("{target}", targetName).replace("{time}", timeLeftString);
        languageManager.sendMessage(sender, message);
    }
}
