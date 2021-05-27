package combatlogx.expansion.loot.protection.object;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.github.sirblobman.api.utility.Validate;

import combatlogx.expansion.loot.protection.listener.ListenerLootProtection;

public class ProtectedItem {
    private final Location location;
    private final ItemStack item;
    private UUID ownerUUID;
    private UUID itemUUID;

    public ProtectedItem(Location location, ItemStack item) {
        this.location = ListenerLootProtection.toBlockLocation(location);
        this.item = Validate.notNull(item, "item must not be null!");
    }

    public ItemStack getItemStack() {
        return item;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getItemUUID() {
        return itemUUID;
    }

    public void setItemUUID(UUID itemUUID) {
        this.itemUUID = itemUUID;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }
}
