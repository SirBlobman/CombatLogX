package com.github.sirblobman.combatlogx.command.combatlogx;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;

public final class CommandCombatLogX extends CombatLogCommand {
    public CommandCombatLogX(ICombatLogX plugin) {
        super(plugin, "combatlogx");
        addSubCommand(new CommandCombatLogXAbout(plugin));
        addSubCommand(new CommandCombatLogXHelp(plugin));
        addSubCommand(new CommandCombatLogXReload(plugin));
        addSubCommand(new CommandCombatLogXTag(plugin));
        addSubCommand(new CommandCombatLogXToggle(plugin));
        addSubCommand(new CommandCombatLogXUntag(plugin));
        addSubCommand(new CommandCombatLogXVersion(plugin));
    }
}
