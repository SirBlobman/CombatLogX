package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.SirBlobman.combatlogx.expansion.cheat.prevention.CheatPrevention;

public class ListenerChat extends CheatPreventionListener {
    public ListenerChat(CheatPrevention expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onChat(AsyncPlayerChatEvent e) {
        FileConfiguration config = getConfig();
        if(!config.getBoolean("disable-chat")) return;

        Player player = e.getPlayer();
        if(!isInCombat(player)) return;

        e.setCancelled(true);
        String message = getMessage("cheat-prevention.no-chat");
        sendMessage(player, message);
    }
}
