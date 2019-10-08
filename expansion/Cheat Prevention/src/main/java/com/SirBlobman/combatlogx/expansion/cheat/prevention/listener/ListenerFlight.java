package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.List;
import java.util.UUID;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.shaded.utility.Util;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class ListenerFlight implements Listener {
    private final Expansion expansion;
    private final ICombatLogX plugin;
    private final List<UUID> preventFallDamage = Util.newList();
    public ListenerFlight(Expansion expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onToggleFlight(PlayerToggleFlightEvent e) {
        if(!e.isFlying()) return;

        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("flight.prevent-flying")) return;

        Player player = e.getPlayer();
        ICombatManager manager = this.plugin.getCombatManager();
        if(!manager.isInCombat(player)) return;

        e.setCancelled(true);
        String message = this.plugin.getLanguageMessageColoredWithPrefix("cheat-prevention.flight.no-flying");
        this.plugin.sendMessage(player, message);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("flight.prevent-flying")) return;

        Player player = e.getPlayer();
        if(!player.isFlying()) return;

        player.setFlying(false);
        this.preventFallDamage.add(player.getUniqueId());

        String message = this.plugin.getLanguageMessageColoredWithPrefix("cheat-prevention.flight.force-disabled");
        this.plugin.sendMessage(player, message);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onDamage(EntityDamageEvent e) {
        EntityDamageEvent.DamageCause cause = e.getCause();
        if(cause != EntityDamageEvent.DamageCause.FALL) return;

        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("flight.prevent-fall-damage")) return;

        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;

        e.setCancelled(true);
    }
}