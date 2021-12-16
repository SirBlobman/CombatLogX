package com.github.sirblobman.combatlogx.command.combatlogx;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;

// TODO
public final class CommandCombatLogXForgive extends CombatLogPlayerCommand {
    public CommandCombatLogXForgive(ICombatLogX plugin) {
        super(plugin, "forgive");
    }
}
