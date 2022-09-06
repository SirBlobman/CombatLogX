package com.github.sirblobman.combatlogx.api.utility;

import org.bukkit.event.entity.PlayerDeathEvent;

import com.github.sirblobman.api.adventure.adventure.text.Component;

public final class PaperHelper {
    public static void setDeathMessage(Component message, PlayerDeathEvent e) {
        net.kyori.adventure.text.Component paperMessage = ComponentConverter.shadedToNormal(message);
        e.deathMessage(paperMessage);
    }
}
