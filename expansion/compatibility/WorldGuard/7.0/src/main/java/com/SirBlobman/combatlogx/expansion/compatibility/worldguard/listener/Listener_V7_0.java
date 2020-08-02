package com.SirBlobman.combatlogx.expansion.compatibility.worldguard.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryMode;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.sk89q.worldguard.bukkit.protection.events.DisallowedPVPEvent;

public class Listener_V7_0 implements Listener {
    private final NoEntryExpansion expansion;
    public Listener_V7_0(NoEntryExpansion expansion) {
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPreventPVP(DisallowedPVPEvent e) {
        NoEntryHandler handler = this.expansion.getNoEntryHandler();
        NoEntryMode mode = handler.getNoEntryMode();
        if(mode != NoEntryMode.VULNERABLE) return;

        ICombatLogX plugin = this.expansion.getPlugin();
        ICombatManager manager = plugin.getCombatManager();

        Player player = e.getDefender();
        if(!manager.isInCombat(player)) return;

        LivingEntity enemy = manager.getEnemy(player);
        if(enemy == null) return;

        e.setCancelled(true);
        this.expansion.sendNoEntryMessage(player, enemy);
    }
}