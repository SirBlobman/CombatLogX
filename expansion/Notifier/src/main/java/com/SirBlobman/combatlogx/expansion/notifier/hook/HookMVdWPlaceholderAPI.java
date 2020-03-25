package com.SirBlobman.combatlogx.expansion.notifier.hook;

import org.bukkit.entity.Player;

import be.maximvdw.placeholderapi.PlaceholderAPI;

public final class HookMVdWPlaceholderAPI {
    public static String replacePlaceholders(Player player, String string) {
        return PlaceholderAPI.replacePlaceholders(player, string);
    }
}