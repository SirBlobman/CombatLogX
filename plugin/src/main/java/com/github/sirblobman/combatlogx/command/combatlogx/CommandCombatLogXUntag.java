package com.github.sirblobman.combatlogx.command.combatlogx;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

public final class CommandCombatLogXUntag extends CombatLogCommand {
    public CommandCombatLogXUntag(ICombatLogX plugin) {
        super(plugin, "tag");
    }
    
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            Set<String> valueSet = getOnlinePlayerNames();
            return getMatching(args[0], valueSet);
        }
        
        return Collections.emptyList();
    }
    
    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if(!checkPermission(sender, "combatlogx.command.combatlogx.untag", true)) {
            return true;
        }
        
        if(args.length < 1) {
            return false;
        }
    
        Player target = findTarget(sender, args[0]);
        if(target == null) {
            return true;
        }
    
        String targetName = target.getName();
        Replacer replacer = message -> message.replace("{target}", targetName);
    
        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        if(!combatManager.isInCombat(target)) {
            sendMessageWithPrefix(sender, "error.target-not-in-combat", replacer, true);
            return true;
        }
    
        combatManager.untag(target, UntagReason.EXPIRE);
        sendMessageWithPrefix(sender, "command.combatlogx.untag-player", replacer, true);
        return true;
    }
}
