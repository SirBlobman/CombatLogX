package com.github.sirblobman.combatlogx.api.expansion.disguise;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;

public final class DisguiseListener extends DisguiseExpansionListener {
    public DisguiseListener(@NotNull DisguiseExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        DisguiseHandler<?> handler = getDisguiseHandler();
        if (handler.hasDisguise(player)) {
            handler.removeDisguise(player);
            LanguageManager languageManager = getLanguageManager();
            languageManager.sendMessageWithPrefix(player, "expansion.disguise-compatibility.remove-disguise");
        }
    }
}
