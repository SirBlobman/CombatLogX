package com.github.sirblobman.combatlogx.command;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;

public final class CommandCombatTimer extends CombatLogPlayerCommand {
    public CommandCombatTimer(ICombatLogX plugin) {
        super(plugin, "combat-timer");
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            Set<String> valueSet = getOnlinePlayerNames();
            return getMatching(args[0], valueSet);
        }

        return Collections.emptyList();
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        if (args.length < 1) {
            checkSelf(player);
            return true;
        }

        Player target = findTarget(player, args[0]);
        if (target == null) return true;

        checkOther(player, target);
        return true;
    }

    private void checkSelf(Player player) {
        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        LanguageManager languageManager = getLanguageManager();

        if (combatManager.isInCombat(player)) {
            double timeLeftMillis = combatManager.getTimerLeftMillis(player);
            double timeLeftSeconds = (timeLeftMillis / 1_000.0D);

            String decimalFormatString = languageManager.getMessage(player, "decimal-format", null,
                    false);
            DecimalFormat decimalFormat = new DecimalFormat(decimalFormatString);
            String timeLeftString = decimalFormat.format(timeLeftSeconds);

            Replacer replacer = message -> message.replace("{time_left}", timeLeftString);
            sendMessageWithPrefix(player, "command.combat-timer.time-left-self", replacer, true);
            return;
        }

        sendMessageWithPrefix(player, "error.self-not-in-combat", null, true);
    }

    private void checkOther(Player player, Player target) {
        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        LanguageManager languageManager = getLanguageManager();

        if (combatManager.isInCombat(target)) {
            double timeLeftMillis = combatManager.getTimerLeftMillis(target);
            double timeLeftSeconds = (timeLeftMillis / 1_000.0D);

            String decimalFormatString = languageManager.getMessage(player, "decimal-format",
                    null, false);
            DecimalFormat decimalFormat = new DecimalFormat(decimalFormatString);
            String timeLeftString = decimalFormat.format(timeLeftSeconds);

            Replacer replacer = message -> message.replace("{time_left}", timeLeftString);
            sendMessageWithPrefix(player, "command.combat-timer.time-left-other", replacer, true);
            return;
        }

        sendMessageWithPrefix(player, "error.target-not-in-combat", null, true);
    }
}
