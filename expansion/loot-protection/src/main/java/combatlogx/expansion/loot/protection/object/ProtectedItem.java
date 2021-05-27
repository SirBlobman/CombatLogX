package combatlogx.expansion.loot.protection.object;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import combatlogx.expansion.loot.protection.listener.ListenerLootProtection;

public class ProtectedItem {

    private final Location location;
    private final ItemStack itemStack;
    private UUID ownerUUID;
    private UUID itemUUID;

    public ProtectedItem(final Location location, final ItemStack itemStack) {
        this.location = ListenerLootProtection.toBlockLocation(location);
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getItemUUID() {
        return itemUUID;
    }

    public void setItemUUID(final UUID itemUUID) {
        this.itemUUID = itemUUID;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(final UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }
}
