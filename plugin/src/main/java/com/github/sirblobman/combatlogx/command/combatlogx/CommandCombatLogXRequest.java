package com.github.sirblobman.combatlogx.command.combatlogx;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;

// TODO
public final class CommandCombatLogXRequest extends CombatLogPlayerCommand {
    public CommandCombatLogXRequest(ICombatLogX plugin) {
        super(plugin, "request");
    }
}
