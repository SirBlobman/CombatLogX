package com.github.sirblobman.combatlogx.command.combatlogx.forgive;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;

import org.jetbrains.annotations.NotNull;

public final class SubCommandForgive extends CombatLogPlayerCommand {
    public SubCommandForgive(@NotNull ICombatLogX plugin) {
        super(plugin, "forgive");
        setPermissionName("combatlogx.command.combatlogx.forgive");
        addSubCommand(new SubCommandForgiveAccept(plugin));
        addSubCommand(new SubCommandForgiveReject(plugin));
        addSubCommand(new SubCommandForgiveRequest(plugin));
        addSubCommand(new SubCommandForgiveToggle(plugin));
    }
}
