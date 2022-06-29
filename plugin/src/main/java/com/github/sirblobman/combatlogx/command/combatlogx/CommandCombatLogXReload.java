package com.github.sirblobman.combatlogx.command.combatlogx;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;

public final class CommandCombatLogXReload extends CombatLogCommand {
    public CommandCombatLogXReload(ICombatLogX plugin) {
        super(plugin, "reload");
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "combatlogx.command.combatlogx.reload", true)) {
            return true;
        }

        ICombatLogX plugin = getCombatLogX();
        plugin.onReload();

        sendMessageWithPrefix(sender, "command.combatlogx.reload-success", null, true);
        return true;
    }
}
