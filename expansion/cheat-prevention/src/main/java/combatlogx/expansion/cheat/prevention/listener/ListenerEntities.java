package combatlogx.expansion.cheat.prevention.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IEntityConfiguration;

public final class ListenerEntities extends CheatPreventionListener {
    public ListenerEntities(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        if (isInCombat(player) && isPreventInteraction()) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.no-entity-interaction");
        }
    }

    private @NotNull IEntityConfiguration getEntityConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getEntityConfiguration();
    }

    private boolean isPreventInteraction() {
        IEntityConfiguration entityConfiguration = getEntityConfiguration();
        return entityConfiguration.isPreventInteraction();
    }
}
