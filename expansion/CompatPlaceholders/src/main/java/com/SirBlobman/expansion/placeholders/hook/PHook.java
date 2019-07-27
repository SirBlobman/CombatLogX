package com.SirBlobman.expansion.placeholders.hook;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PHook extends PlaceholderExpansion implements IPlaceholderHandler {
    public boolean persist() {
        return true;
    }

    public String getIdentifier() {
        return "combatlogx";
    }

    public String getAuthor() {
        return "SirBlobman";
    }

    public String getVersion() {
        return "13.1";
    }

    @Override
    public String onPlaceholderRequest(Player player, String id) {
        return handlePlaceholder(player, id);
    }
}