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
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

public final class CommandCombatLogXTag extends CombatLogCommand {
    public CommandCombatLogXTag(ICombatLogX plugin) {
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
        if(!checkPermission(sender, "combatlogx.command.combatlogx.tag", true)) {
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
        boolean successfulTag = combatManager.tag(target, null, TagType.UNKNOWN, TagReason.UNKNOWN);
        String messagePath = ("command.combatlogx." + (successfulTag ? "tag-player" : "tag-failure"));
    
        sendMessageWithPrefix(sender, messagePath, replacer, true);
        return true;
    }
}
