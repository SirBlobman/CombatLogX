package combatlogx.expansion.cheat.prevention.listener.legacy;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IItemConfiguration;
import combatlogx.expansion.cheat.prevention.listener.CheatPreventionListener;

public final class ListenerLegacyItemPickup extends CheatPreventionListener {
    public ListenerLegacyItemPickup(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPickup(PlayerPickupItemEvent e) {
        if (isPreventPickup()) {
            Player player = e.getPlayer();
            if (!isInCombat(player)) {
                return;
            }

            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.items.no-pickup");
        }
    }

    private @NotNull IItemConfiguration getItemConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getItemConfiguration();
    }

    private boolean isPreventPickup() {
        IItemConfiguration itemConfiguration = getItemConfiguration();
        return itemConfiguration.isPreventPickup();
    }
}
