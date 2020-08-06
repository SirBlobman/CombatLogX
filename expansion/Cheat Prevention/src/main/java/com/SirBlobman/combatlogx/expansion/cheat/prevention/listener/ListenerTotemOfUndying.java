package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityResurrectEvent;

import com.SirBlobman.combatlogx.expansion.cheat.prevention.CheatPrevention;

public class ListenerTotemOfUndying extends CheatPreventionListener {
    public ListenerTotemOfUndying(CheatPrevention expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onResurrect(EntityResurrectEvent e) {
        LivingEntity entity = e.getEntity();
        if(!(entity instanceof Player)) return;

        FileConfiguration config = getConfig();
        if(!config.getBoolean("items.prevent-totem-usage")) return;

        Player player = (Player) entity;
        if(!isInCombat(player)) return;

        e.setCancelled(true);
        String message = getMessage("cheat-prevention.no-totem");
        sendMessage(player, message);
    }
}