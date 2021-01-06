package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.SirBlobman.combatlogx.api.event.PlayerReTagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.expansion.cheat.prevention.CheatPrevention;

public class ListenerFlight extends CheatPreventionListener {
    private final Set<UUID> preventFallDamage;
    private final Set<UUID> reEnableSet;
    public ListenerFlight(CheatPrevention expansion) {
        super(expansion);
        this.preventFallDamage = new HashSet<>();
        this.reEnableSet = new HashSet<>();
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        checkFlight(player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onReTag(PlayerReTagEvent e) {
        Player player = e.getPlayer();
        checkFlight(player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onUntag(PlayerUntagEvent e) {
        FileConfiguration config = getConfig();
        if(!config.getBoolean("flight.re-enable-flight")) return;

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if(this.reEnableSet.contains(uuid)) {
            player.setAllowFlight(true);
            this.reEnableSet.remove(uuid);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
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

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
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

    private void checkFlight(Player player) {
        FileConfiguration config = getConfig();
        if(!config.getBoolean("flight.prevent-flying")) return;

        if(!player.getAllowFlight()) return;
        UUID uuid = player.getUniqueId();
        this.reEnableSet.add(uuid);

        player.setFlying(false);
        player.setAllowFlight(false);
        if(config.getBoolean("flight.prevent-fall-damage")) {
            this.preventFallDamage.add(uuid);
        }

        String message = getMessage("cheat-prevention.flight.force-disabled");
        sendMessage(player, message);
    }
}