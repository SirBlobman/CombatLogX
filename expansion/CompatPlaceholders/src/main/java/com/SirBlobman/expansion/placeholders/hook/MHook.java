package com.SirBlobman.expansion.placeholders.hook;

import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.CombatLogX;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

public class MHook implements IPlaceholderHandler, PlaceholderReplacer {
    public void register() {
        PlaceholderAPI.registerPlaceholder(CombatLogX.INSTANCE, "combatlogx_time_left", this);
        PlaceholderAPI.registerPlaceholder(CombatLogX.INSTANCE, "combatlogx_enemy_name", this);
        PlaceholderAPI.registerPlaceholder(CombatLogX.INSTANCE, "combatlogx_enemy_health", this);
        PlaceholderAPI.registerPlaceholder(CombatLogX.INSTANCE, "combatlogx_enemy_health_rounded", this);
        PlaceholderAPI.registerPlaceholder(CombatLogX.INSTANCE, "combatlogx_enemy_hearts", this);
        PlaceholderAPI.registerPlaceholder(CombatLogX.INSTANCE, "combatlogx_in_combat", this);
        PlaceholderAPI.registerPlaceholder(CombatLogX.INSTANCE, "combatlogx_status", this);
    }
    
    @Override
    public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
        Player player = e.getPlayer();
        if(player != null) {
            String id = e.getPlaceholder();
            if(id.startsWith("combatlogx_")) {
                String placeholder = id.substring("combatlogx_".length());
                return handlePlaceholder(player, placeholder);
            }
        }
        
        return null;
    }
}