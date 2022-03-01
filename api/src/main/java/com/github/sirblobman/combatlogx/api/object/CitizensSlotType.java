package com.github.sirblobman.combatlogx.api.object;

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
     * Represents an item from the offhand of an inventory
     */
    OFFHAND
}
