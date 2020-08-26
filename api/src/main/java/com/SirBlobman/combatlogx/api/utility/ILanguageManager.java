package com.SirBlobman.combatlogx.api.utility;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.SirBlobman.api.utility.MessageUtil;

public interface ILanguageManager {
    String getLanguage();
    String getMessage(String key);

    String getLocalizedMessage(Player player, String key);
    void sendLocalizedMessage(Player player, String key, Replacer... replacerArray);
    
    default String getMessageColored(String key) {
        String message = getMessage(key);
        if(message == null || message.isEmpty()) return "";
        return MessageUtil.color(message);
    }
    
    default String getMessageColoredWithPrefix(String key) {
        String message = getMessageColored(key);
        if(message == null || message.isEmpty()) return "";
        
        String prefix = getMessageColored("prefixes.plugin");
        if(prefix == null || prefix.isEmpty()) return message;
        
        return (prefix + " " + message);
    }
    
    default void sendMessage(CommandSender sender, String... messageArray) {
        for(String message : messageArray) {
            if(message == null || message.isEmpty()) continue;
            sender.sendMessage(message);
        }
    }
}
