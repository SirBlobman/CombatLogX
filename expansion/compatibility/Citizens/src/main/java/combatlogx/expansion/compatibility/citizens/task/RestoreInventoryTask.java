package combatlogx.expansion.compatibility.citizens.task;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.folia.details.EntityTaskDetails;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.manager.InventoryManager;

/**
 * Gives a player their stored inventory back one tick after they respawned.
 * Used for the 'death-inventory: KEEP' mode, because items set on a dead player are wiped on respawn.
 */
public final class RestoreInventoryTask extends EntityTaskDetails<Player> {
    private final CitizensExpansion expansion;

    public RestoreInventoryTask(@NotNull CitizensExpansion expansion, @NotNull Player entity) {
        super(expansion.getPlugin().getPlugin(), entity);
        setDelay(1L);
        this.expansion = expansion;
    }

    @Override
    public void run() {
        Player player = getEntity();
        if (player == null || !player.isOnline()) {
            return;
        }

        InventoryManager inventoryManager = this.expansion.getInventoryManager();
        inventoryManager.restoreInventory(player);
    }
}
