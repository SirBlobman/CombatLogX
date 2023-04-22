package com.github.sirblobman.combatlogx.command;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.DoubleReplacer;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

import org.jetbrains.annotations.NotNull;

public final class CommandCombatTimer extends CombatLogPlayerCommand {
    public CommandCombatTimer(@NotNull ICombatLogX plugin) {
        super(plugin, "combat-timer");
        setPermissionName("combatlogx.command.combat-timer");
    }

    @Override
    protected @NotNull List<String> onTabComplete(@NotNull Player player, String @NotNull [] args) {
        if (args.length == 1) {
            Set<String> valueSet = getOnlinePlayerNames();
            return getMatching(args[0], valueSet);
        }

        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NotNull Player player, String @NotNull [] args) {
        if (args.length < 1) {
            checkSelf(player);
            return true;
        }

        Player target = findTarget(player, args[0]);
        if (target == null) {
            return true;
        }

        checkOther(player, target);
        return true;
    }

    private void checkSelf(@NotNull Player player) {
        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        LanguageManager languageManager = getLanguageManager();

        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null || tagInformation.isExpired()) {
            sendMessageWithPrefix(player, "error.self-not-in-combat");
            return;
        }

        double timeLeftMillis = tagInformation.getMillisLeftCombined();
        double timeLeftSeconds = (timeLeftMillis / 1_000.0D);
        DecimalFormat decimalFormat = languageManager.getDecimalFormat(player);

        Replacer replacer = new DoubleReplacer("{time_left}", timeLeftSeconds, decimalFormat);
        sendMessageWithPrefix(player, "command.combat-timer.time-left-self", replacer);
    }

    private void checkOther(@NotNull Player player, @NotNull Player target) {
        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        LanguageManager languageManager = getLanguageManager();
        String targetName = target.getName();

        TagInformation tagInformation = combatManager.getTagInformation(target);
        if (tagInformation == null || tagInformation.isExpired()) {
            Replacer replacer = new StringReplacer("{target}", targetName);
            sendMessageWithPrefix(player, "error.target-not-in-combat", replacer);
            return;
        }

        double timeLeftMillis = tagInformation.getMillisLeftCombined();
        double timeLeftSeconds = (timeLeftMillis / 1_000.0D);
        DecimalFormat decimalFormat = languageManager.getDecimalFormat(player);

        Replacer timeLeftReplacer = new DoubleReplacer("{time_left}", timeLeftSeconds, decimalFormat);
        Replacer targetNameReplacer = new StringReplacer("{target}", targetName);
        sendMessageWithPrefix(player, "command.combat-timer.time-left-other", timeLeftReplacer,
                targetNameReplacer);
    }
}
