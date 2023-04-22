package com.github.sirblobman.combatlogx.command.combatlogx;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;
import com.github.sirblobman.combatlogx.command.combatlogx.forgive.SubCommandForgive;

import org.jetbrains.annotations.NotNull;

public final class CommandCombatLogX extends CombatLogCommand {
    public CommandCombatLogX(@NotNull ICombatLogX plugin) {
        super(plugin, "combatlogx");
        setPermissionName("combatlogx.command.combatlogx");

        addSubCommand(new SubCommandAbout(plugin));
        addSubCommand(new SubCommandForgive(plugin));
        addSubCommand(new SubCommandHelp(plugin));
        addSubCommand(new SubCommandReload(plugin));
        addSubCommand(new SubCommandTag(plugin));
        addSubCommand(new SubCommandToggle(plugin));
        addSubCommand(new SubCommandUntag(plugin));
        addSubCommand(new SubCommandVersion(plugin));
    }
}
