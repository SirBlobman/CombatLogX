package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityToggleGlideEvent;

import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.expansion.cheat.prevention.CheatPrevention;

public class ListenerElytra extends CheatPreventionListener {
    public ListenerElytra(CheatPrevention expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        if(!player.isGliding()) return;

        FileConfiguration config = getConfig();
        if(!config.getBoolean("items.prevent-elytra")) return;

        player.setGliding(false);
        String message = getMessage("cheat-prevention.elytra.force-disabled");
        sendMessage(player, message);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onToggleGlide(EntityToggleGlideEvent e) {
        if(!e.isGliding()) return;

        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;

        FileConfiguration config = getConfig();
        if(!config.getBoolean("items.prevent-elytra")) return;

        Player player = (Player) entity;
        if(!isInCombat(player)) return;

        e.setCancelled(true);
        String message = getMessage("cheat-prevention.elytra.no-gliding");
        sendMessage(player, message);
    }
}