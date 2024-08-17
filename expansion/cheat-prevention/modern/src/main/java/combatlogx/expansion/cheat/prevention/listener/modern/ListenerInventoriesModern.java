package combatlogx.expansion.cheat.prevention.listener.modern;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

import com.github.sirblobman.combatlogx.api.event.PlayerReTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IInventoryConfiguration;
import combatlogx.expansion.cheat.prevention.listener.CheatPreventionListener;

// 1.20.6 use a InventoryView interface instead of an abstract class
// No code difference, only compilation difference.
public final class ListenerInventoriesModern extends CheatPreventionListener {
    public ListenerInventoriesModern(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        if (isClose()) {
            Player player = e.getPlayer();
            InventoryView openView = player.getOpenInventory();
            InventoryType viewType = openView.getType();
            player.closeInventory();

            if (isMessage(viewType)) {
                sendMessage(player, "expansion.cheat-prevention.inventory.force-closed");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onReTag(PlayerReTagEvent e) {
        if (isCloseOnRetag()) {
            Player player = e.getPlayer();
            InventoryView openView = player.getOpenInventory();
            InventoryType viewType = openView.getType();
            player.closeInventory();

            if (isMessage(viewType)) {
                sendMessage(player, "expansion.cheat-prevention.inventory.force-closed");
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onOpen(InventoryOpenEvent e) {
        HumanEntity human = e.getPlayer();
        if (!(human instanceof Player)) {
            return;
        }

        Player player = (Player) human;
        if (isPreventOpening() && isInCombat(player)) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.inventory.no-opening");
        }
    }

    private @NotNull IInventoryConfiguration getInventoryConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getInventoryConfiguration();
    }

    private boolean isClose() {
        IInventoryConfiguration inventoryConfiguration = getInventoryConfiguration();
        return inventoryConfiguration.isClose();
    }

    private boolean isCloseOnRetag() {
        IInventoryConfiguration inventoryConfiguration = getInventoryConfiguration();
        return inventoryConfiguration.isCloseOnRetag();
    }

    private boolean isPreventOpening() {
        IInventoryConfiguration inventoryConfiguration = getInventoryConfiguration();
        return inventoryConfiguration.isPreventOpening();
    }

    private boolean isMessage(@NotNull InventoryType type) {
        IInventoryConfiguration inventoryConfiguration = getInventoryConfiguration();
        return !inventoryConfiguration.isNoMessage(type);
    }
}
