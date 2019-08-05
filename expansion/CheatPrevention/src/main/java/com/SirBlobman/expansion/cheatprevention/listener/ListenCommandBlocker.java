package com.SirBlobman.expansion.cheatprevention.listener;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;

public class ListenCommandBlocker implements Listener {
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        String command = e.getMessage();
        String actualCommand = convertCommand(command);
        if(!isBlocked(actualCommand)) return;
        
        e.setCancelled(true);
        
        String format = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.command.not allowed");
        String message = format.replace("{command}", actualCommand);
        Util.sendMessage(player, message);
    }
    
    private String convertCommand(String original) {
        if(original == null || original.isEmpty()) original = "";
        if(!original.startsWith("/")) original = "/" + original;
        
        return original;
    }
    
    private boolean isBlocked(String command) {
    	List<String> commandList = ConfigCheatPrevention.BLOCKED_COMMANDS_LIST;
    	boolean contains = listContainsOrStartsWith(commandList, command);
    	
    	return (ConfigCheatPrevention.BLOCKED_COMMANDS_IS_WHITELIST ? !contains : contains);
    }
    
    private boolean listContainsOrStartsWith(List<String> list, String query) {
    	if(list.contains(query)) {
    		debug("Command List contains '" + query + "'.");
    		return true;
    	}
    	
    	for(String string : list) {
    		if(query.startsWith(string)) {
    			debug("'" + query + "' starts with '" + string + "'.");
    			return true;
    		}
    	}
    	
    	debug("Could not find '" + query + "' in command list.");
    	return false;
    }
    
    private void debug(String message) {
    	Util.debug("[Cheat Prevention] [Command Blocker] " + message);
    }
}