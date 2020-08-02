package com.SirBlobman.combatlogx.expansion.notifier.hook;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public final class HookPlaceholderAPI {
    public static String replacePlaceholders(Player player, String string) {
        return PlaceholderAPI.setPlaceholders(player, string);
    }
}