package com.SirBlobman.expansion.notifier.hook;

import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.placeholders.hook.IPlaceholderHandler;

import java.util.List;

public class PlaceholderHandler implements IPlaceholderHandler {
    public String replaceAllPlaceholders(Player player, String text) {
        List<String> placeholders = Util.newList("time_left", "in_combat", "status", "enemy_name", "enemy_health", "enemy_health_rounded", "enemy_hearts");
        for(String placeholder : placeholders) {
            String format = "{" + placeholder + "}";
            if(text.contains(format)) {
                String replace = handlePlaceholder(player, placeholder);
                text = text.replace(format, replace);
            }
        }
        
        return text;
    }
}