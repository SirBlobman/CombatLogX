package com.github.sirblobman.combatlogx.command.combatlogx;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

public final class SubCommandUntag extends CombatLogCommand {
    public SubCommandUntag(ICombatLogX plugin) {
        super(plugin, "untag");
        setPermissionName("combatlogx.command.combatlogx.untag");
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            Set<String> valueSet = getOnlinePlayerNames();
            return getMatching(args[0], valueSet);
        }

        return Collections.emptyList();
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            return false;
        }

        Player target = findTarget(sender, args[0]);
        if (target == null) {
            return true;
        }

        String targetName = target.getName();
        Replacer replacer = new StringReplacer("{target}", targetName);

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        if (!combatManager.isInCombat(target)) {
            sendMessageWithPrefix(sender, "error.target-not-in-combat", replacer);
            return true;
        }

        combatManager.untag(target, UntagReason.EXPIRE);
        sendMessageWithPrefix(sender, "command.combatlogx.untag-player", replacer);
        return true;
    }
}
