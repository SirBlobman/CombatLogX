package com.github.sirblobman.combatlogx.api.object;

/**
 * Citizens slot type, used for the {@link com.github.sirblobman.combatlogx.api.event.NPCDropItemEvent}
 * @author SizzleMcGrizzle
 */
public enum CitizensSlotType {
    /**
     * Represents an item from the armor content of an inventory
     */
    ARMOR,

    /**
     * Represents an item from the main container of an inventory (excludes offhand, includes mainhand)
     */
    INVENTORY,

    /**
     * Represents an item from the off-hand of an inventory
     */
    OFFHAND
}
