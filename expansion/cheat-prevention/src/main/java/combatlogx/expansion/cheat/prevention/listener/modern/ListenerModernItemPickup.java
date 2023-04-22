package combatlogx.expansion.cheat.prevention.listener.modern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IItemConfiguration;
import combatlogx.expansion.cheat.prevention.listener.CheatPreventionListener;
import org.jetbrains.annotations.NotNull;

public final class ListenerModernItemPickup extends CheatPreventionListener {
    public ListenerModernItemPickup(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        if (isPreventPickup() && isInCombat(player)) {
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
