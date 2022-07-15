package com.github.sirblobman.combatlogx.command.combatlogx;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;

// TODO
public final class SubCommandForgive extends CombatLogPlayerCommand {
    public SubCommandForgive(ICombatLogX plugin) {
        super(plugin, "forgive");
        setPermissionName("combatlogx.command.combatlogx.forgive");
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        // TODO: Combat Forgive System (Request/Accept/Deny)
        return false;
    }
}
