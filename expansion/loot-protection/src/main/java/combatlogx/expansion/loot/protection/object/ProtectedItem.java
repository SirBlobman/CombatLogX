package combatlogx.expansion.loot.protection.object;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.github.sirblobman.api.location.BlockLocation;
import com.github.sirblobman.api.utility.Validate;

public class ProtectedItem {
    private final BlockLocation location;
    private final ItemStack item;
    private UUID ownerUUID;
    private UUID itemUUID;

    public ProtectedItem(BlockLocation location, ItemStack item) {
        this.location = Validate.notNull(location, "location must not be null!");
        this.item = Validate.notNull(item, "item must not be null!");
    }

    public ProtectedItem(Location location, ItemStack item) {
        this(BlockLocation.from(location), item);
    }

    public ItemStack getItemStack() {
        return this.item;
    }

    public UUID getItemUUID() {
        return this.itemUUID;
    }

    public void setItemUUID(UUID itemUUID) {
        this.itemUUID = itemUUID;
    }

    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }
}
