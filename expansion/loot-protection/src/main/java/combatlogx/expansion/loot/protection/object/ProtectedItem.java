package combatlogx.expansion.loot.protection.object;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.github.sirblobman.api.object.WorldXYZ;
import com.github.sirblobman.api.utility.Validate;

public class ProtectedItem {
    private final WorldXYZ location;
    private final ItemStack item;
    private UUID ownerUUID;
    private UUID itemUUID;

    public ProtectedItem(WorldXYZ location, ItemStack item) {
        this.location = Validate.notNull(location, "location must not be null!");
        this.item = Validate.notNull(item, "item must not be null!");
    }

    public ProtectedItem(Location location, ItemStack item) {
        this(WorldXYZ.from(location), item);
    }

    public ItemStack getItemStack() {
        return item;
    }

    public WorldXYZ getLocation() {
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
