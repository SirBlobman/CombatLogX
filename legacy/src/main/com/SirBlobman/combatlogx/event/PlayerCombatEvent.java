package com.SirBlobman.combatlogx.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerCombatEvent extends CombatEvent {
    private static HandlerList HL = new HandlerList();
    private final Player player;
    
    public PlayerCombatEvent(Player player, LivingEntity le2, boolean isPlayerAttacker) {
        super(player, le2, isPlayerAttacker);
        this.player = player;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
    
    public static HandlerList getHandlerList() {
        return HL;
    }
}