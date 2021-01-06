package com.SirBlobman.combatlogx.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.api.shaded.command.CustomCommand;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;
import com.SirBlobman.combatlogx.api.utility.Replacer;

public class CommandCombatTimer extends CustomCommand<CombatLogX> {
    public CommandCombatTimer(CombatLogX plugin) {
        super(plugin, "combat-timer");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(args.length > 0) {
            String targetName = args[0];
            Player target = getCustomTarget(sender, targetName);
            if(target == null) return true;

            checkOther(sender, target);
            return true;
        }

        if(!(sender instanceof Player)) {
            sendMessage(sender, "errors.not-player");
            return true;
        }

        Player player = (Player) sender;
        checkSelf(player);
        return true;
    }

    private void checkSelf(Player player) {
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) {
            sendMessage(player, "errors.self-not-in-combat");
            return;
        }

        int timeLeft = combatManager.getTimerSecondsLeft(player);
        String timeLeftString = Integer.toString(timeLeft);
        sendMessage(player, "commands.combat-timer.time-left.self", message -> message.replace("{time}", timeLeftString));
    }

    private void checkOther(CommandSender sender, Player target) {
        String targetName = target.getName();
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(target)) {
            sendMessage(sender, "errors.target-not-in-combat", message -> message.replace("{target}", targetName));
            return;
        }

        int timeLeft = combatManager.getTimerSecondsLeft(target);
        String timeLeftString = Integer.toString(timeLeft);
        sendMessage(sender, "commands.combat-timer.time-left.other", message -> message.replace("{target}", targetName).replace("{time}", timeLeftString));
    }

    private void sendMessage(CommandSender sender, String key, Replacer... replacerArray) {
        ILanguageManager languageManager = this.plugin.getLanguageManager();
        if(sender instanceof Player) {
            Player player = (Player) sender;
            languageManager.sendLocalizedMessage(player, key, replacerArray);
            return;
        }

        String message = languageManager.getMessageColoredWithPrefix(key);
        for(Replacer replacer : replacerArray) message = replacer.replace(message);
        languageManager.sendMessage(sender, message);
    }

    private Player getCustomTarget(CommandSender sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if(target == null) {
            sendMessage(sender, "errors.invalid-target", message -> message.replace("{target}", targetName));
            return null;
        }
        return target;
    }
}
