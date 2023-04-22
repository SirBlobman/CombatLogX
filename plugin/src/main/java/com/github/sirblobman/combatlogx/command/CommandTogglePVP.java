package com.github.sirblobman.combatlogx.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;

import org.jetbrains.annotations.NotNull;

public final class CommandTogglePVP extends CombatLogCommand {
    public CommandTogglePVP(@NotNull ICombatLogX plugin) {
        super(plugin, "togglepvp");
        setPermissionName("combatlogx.command.togglepvp");
    }

    @Override
    protected @NotNull List<String> onTabComplete(@NotNull CommandSender sender, String @NotNull [] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, String @NotNull [] args) {
        Replacer replacer = new StringReplacer("{value}", "Newbie Helper");
        sendMessageWithPrefix(sender, "error.unknown-expansion", replacer);
        return true;
    }
}
