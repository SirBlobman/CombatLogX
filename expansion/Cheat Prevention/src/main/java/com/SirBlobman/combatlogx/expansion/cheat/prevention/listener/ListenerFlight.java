package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.expansion.cheat.prevention.CheatPrevention;

public class ListenerFlight extends CheatPreventionListener {
    private final List<UUID> preventFallDamage;
    public ListenerFlight(CheatPrevention expansion) {
        super(expansion);
        this.preventFallDamage = new ArrayList<>();
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        if(!player.isFlying()) return;

        FileConfiguration config = getConfig();
        if(!config.getBoolean("flight.prevent-flying")) return;

        if(config.getBoolean("flight.force-disable-flight")) player.setAllowFlight(false);
        player.setFlying(false);

        if(config.getBoolean("flight.prevent-fall-damage")) {
            UUID uuid = player.getUniqueId();
            this.preventFallDamage.add(uuid);
        }

        String message = getMessage("cheat-prevention.flight.force-disabled");
        sendMessage(player, message);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onToggleFlight(PlayerToggleFlightEvent e) {
        if(!e.isFlying()) return;

        FileConfiguration config = getConfig();
        if(!config.getBoolean("flight.prevent-flying")) return;

        Player player = e.getPlayer();
        if(!isInCombat(player)) return;

        e.setCancelled(true);
        String message = getMessage("cheat-prevention.flight.no-flying");
        sendMessage(player, message);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onDamage(EntityDamageEvent e) {
        DamageCause damageCause = e.getCause();
        if(damageCause != DamageCause.FALL) return;

        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;

        FileConfiguration config = getConfig();
        if(!config.getBoolean("flight.prevent-fall-damage")) return;
    
        Player player = (Player) entity;
        UUID uuid = player.getUniqueId();
        if(!this.preventFallDamage.contains(uuid)) return;
        
        e.setCancelled(true);
        this.preventFallDamage.remove(uuid);
    }
}