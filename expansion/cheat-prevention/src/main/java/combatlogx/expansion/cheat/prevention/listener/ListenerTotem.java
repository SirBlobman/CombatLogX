package combatlogx.expansion.cheat.prevention.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityResurrectEvent;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IItemConfiguration;

public final class ListenerTotem extends CheatPreventionListener {
    public ListenerTotem(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onResurrect(EntityResurrectEvent e) {
        LivingEntity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        if (isInCombat(player) && isPreventTotem()) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.no-totem");
        }
    }

    private @NotNull IItemConfiguration getItemConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getItemConfiguration();
    }

    private boolean isPreventTotem() {
        IItemConfiguration itemConfiguration = getItemConfiguration();
        return itemConfiguration.isPreventTotem();
    }
}
