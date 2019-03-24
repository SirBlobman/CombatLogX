package com.SirBlobman.expansion.cheatprevention.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;

import java.util.List;

public class ListenCommandBlocker implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled=true)
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
    
    private String getMainCommand(String original) {
        if(original == null || original.isEmpty()) return "";
        
        int firstSpace = original.indexOf('\u0020');
        if(firstSpace < 0) return original;
        
        return original.substring(0, firstSpace);
    }
    
    private boolean isBlocked(String command) {
        String mainCommand = getMainCommand(command);
        
        if(ConfigCheatPrevention.BLOCKED_COMMANDS_IS_WHITELIST) {
            List<String> allowedCommands = ConfigCheatPrevention.BLOCKED_COMMANDS_LIST;
            return (!allowedCommands.contains(mainCommand) && !allowedCommands.contains(command));
        }
        
        List<String> blockedCommands = ConfigCheatPrevention.BLOCKED_COMMANDS_LIST;
        return (blockedCommands.contains(mainCommand) || blockedCommands.contains(command));
    }
}