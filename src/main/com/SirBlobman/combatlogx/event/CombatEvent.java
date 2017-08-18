package com.SirBlobman.combatlogx.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CombatEvent extends Event implements Cancellable {
    private static HandlerList HL = new HandlerList();
    private boolean isCancelled;
    
    /**
     * An event that will be called when two entities attack
     * each other and both of them can be tagged<br/>
     * This version does not detect if an entity is a player or not
     * @param le1 The 1<sup>st</sup> entity which is involved
     * @param le2 The 2<sup>nd</sup> entity which is involved
     * @param attacker Is the first entity the attacker?
     */
    private final LivingEntity entity1, entity2;
    private final boolean isFirstEntityAttacker;
    public CombatEvent(LivingEntity le1, LivingEntity le2, boolean attacker) {
        this.entity1 = le1;
        this.entity2 = le2;
        this.isFirstEntityAttacker = attacker;
    }

    @Override
    public boolean isCancelled() {return isCancelled;}
    
    @Override
    public void setCancelled(boolean b) {this.isCancelled = b;}

    @Override
    public HandlerList getHandlers() {return getHandlerList();}
    public static HandlerList getHandlerList() {return HL;}
    
    public LivingEntity getAttacker() {
        if(isFirstEntityAttacker) return entity1;
        else return entity2;
    }
    
    public LivingEntity getTarget() {
        if(isFirstEntityAttacker) return entity2;
        else return entity1;
    }
}