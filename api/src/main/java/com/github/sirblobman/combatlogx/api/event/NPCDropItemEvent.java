package com.github.sirblobman.combatlogx.api.event;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import com.github.sirblobman.combatlogx.api.object.CitizensSlotType;

import org.jetbrains.annotations.NotNull;

/**
 * A custom event that will be called when an item is dropped from a combat logged NPC.
 *
 * @author SizzleMcGrizzle
 */
public final class NPCDropItemEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST;

    static {
        HANDLER_LIST = new HandlerList();
    }

    private final OfflinePlayer player;
    private final Location location;
    private final CitizensSlotType slotType;
    private boolean cancelled;
    private ItemStack item;

    public NPCDropItemEvent(@NotNull ItemStack item, @NotNull OfflinePlayer player, @NotNull Location location,
                            @NotNull CitizensSlotType slotType) {
        this.player = player;
        this.location = location;
        this.item = item;
        this.slotType = slotType;
        this.cancelled = false;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    /**
     * @return The player who combat logged
     */
    public @NotNull OfflinePlayer getPlayer() {
        return player;
    }

    /**
     * @return The location at which the player combat logged
     */
    public @NotNull Location getLocation() {
        return location;
    }

    /**
     * @return The item being dropped from the location
     * @see CitizensSlotType for various inventory slot locations this item could be from
     */
    public @NotNull ItemStack getItem() {
        return item;
    }

    public void setItem(@NotNull ItemStack item) {
        this.item = item.clone();
    }

    /**
     * @return The type of slot the item is from
     */
    public @NotNull CitizensSlotType getSlotType() {
        return slotType;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
