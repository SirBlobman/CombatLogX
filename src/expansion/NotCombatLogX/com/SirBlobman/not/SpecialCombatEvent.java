package com.SirBlobman.not;

import com.SirBlobman.combatlogx.event.CombatEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class SpecialCombatEvent extends CombatEvent {
    private static HandlerList HL = new HandlerList();
    private final Player player;
    public SpecialCombatEvent(Player p) {
        super(p, null, false);
        this.player = p;
    }
    
    public Player getPlayer() {return player;}
    
    @Override
    public HandlerList getHandlers() {return getHandlerList();}
    public static HandlerList getHandlerList() {return HL;}
}