package com.SirBlobman.combatlogx.api.utility;

import org.bukkit.command.CommandSender;

import com.SirBlobman.api.utility.MessageUtil;

public interface ILanguageManager {
    String getLanguage();
    String getMessage(String key);
    
    default String getMessageColored(String key) {
        String message = getMessage(key);
        if(message == null || message.isEmpty()) return "";
        return MessageUtil.color(message);
    }
    
    default String getMessageColoredWithPrefix(String key) {
        String message = getMessageColored(key);
        if(message == null || message.isEmpty()) return "";
        
        String prefix = getMessageColored("prefixes-plugin");
        return (prefix + " " + message);
    }
    
    default void sendMessage(CommandSender sender, String... messageArray) {
        for(String message : messageArray) {
            if(message == null || message.isEmpty()) continue;
            sender.sendMessage(message);
        }
    }
}
