package com.github.sirblobman.combatlogx.command.combatlogx.forgive;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;
import com.github.sirblobman.combatlogx.api.manager.IForgiveManager;

public final class SubCommandForgiveToggle extends CombatLogPlayerCommand {
    public SubCommandForgiveToggle(ICombatLogX plugin) {
        super(plugin, "toggle");
        setPermissionName("combatlogx.command.combatlogx.forgive.toggle");
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        ICombatLogX combatLogX = getCombatLogX();
        IForgiveManager forgiveManager = combatLogX.getForgiveManager();
        boolean toggleValue = forgiveManager.getToggleValue(player);

        if (toggleValue) {
            forgiveManager.setToggle(player, false);
            sendMessage(player, "command.combatlogx.forgive.toggle-enable");
        } else {
            forgiveManager.setToggle(player, true);
            sendMessage(player, "command.combatlogx.forgive.toggle-disable");
        }

        return true;
    }
}
