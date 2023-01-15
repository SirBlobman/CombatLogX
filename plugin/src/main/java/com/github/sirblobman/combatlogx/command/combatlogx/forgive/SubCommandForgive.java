package com.github.sirblobman.combatlogx.command.combatlogx.forgive;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;

public final class SubCommandForgive extends CombatLogPlayerCommand {
    public SubCommandForgive(ICombatLogX plugin) {
        super(plugin, "forgive");
        setPermissionName("combatlogx.command.combatlogx.forgive");
        addSubCommand(new SubCommandForgiveAccept(plugin));
        addSubCommand(new SubCommandForgiveReject(plugin));
        addSubCommand(new SubCommandForgiveRequest(plugin));
        addSubCommand(new SubCommandForgiveToggle(plugin));
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        return false;
    }
}
