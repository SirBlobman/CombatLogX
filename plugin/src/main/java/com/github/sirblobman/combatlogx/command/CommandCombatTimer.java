package com.github.sirblobman.combatlogx.command;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.command.PlayerCommand;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.combatlogx.CombatPlugin;
import com.github.sirblobman.combatlogx.manager.CombatManager;

public class CommandCombatTimer extends PlayerCommand {
    private final CombatPlugin plugin;

    public CommandCombatTimer(CombatPlugin plugin) {
        super(plugin, "combat-timer");
        this.plugin = plugin;
    }

    @Override
    public LanguageManager getLanguageManager() {
        return this.plugin.getLanguageManager();
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        if(args.length == 1) {
            Set<String> valueSet = getOnlinePlayerNames();
            return getMatching(valueSet, args[0]);
        }

        return Collections.emptyList();
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if(args.length < 1) {
            checkSelf(player);
            return true;
        }

        Player target = findTarget(player, args[0]);
        if(target == null) return true;

        checkOther(player, target);
        return true;
    }

    private void checkSelf(Player player) {
        CombatManager combatManager = this.plugin.getCombatManager();
        LanguageManager languageManager = getLanguageManager();
        if(combatManager.isInCombat(player)) {
            double timeLeftMillis = combatManager.getTimerLeftMillis(player);
            double timeLeftSeconds = (timeLeftMillis / 1_000.0D);

            String decimalFormatString = languageManager.getMessage(player, "decimal-format");
            DecimalFormat decimalFormat = new DecimalFormat(decimalFormatString);
            String timeLeftString = decimalFormat.format(timeLeftSeconds);

            Replacer replacer = message -> message.replace("{time_left}", timeLeftString);
            languageManager.sendMessage(player, "command.combat-timer.time-left-self", replacer, true);
            return;
        }

        languageManager.sendMessage(player, "error.self-not-in-combat", null, true);
    }

    private void checkOther(Player player, Player target) {
        CombatManager combatManager = this.plugin.getCombatManager();
        LanguageManager languageManager = getLanguageManager();
        if(combatManager.isInCombat(target)) {
            double timeLeftMillis = combatManager.getTimerLeftMillis(target);
            double timeLeftSeconds = (timeLeftMillis / 1_000.0D);

            String decimalFormatString = languageManager.getMessage(player, "decimal-format");
            DecimalFormat decimalFormat = new DecimalFormat(decimalFormatString);
            String timeLeftString = decimalFormat.format(timeLeftSeconds);

            Replacer replacer = message -> message.replace("{time_left}", timeLeftString);
            languageManager.sendMessage(player, "command.combat-timer.time-left-other", replacer, true);
            return;
        }

        languageManager.sendMessage(player, "error.target-not-in-combat", null, true);
    }
}