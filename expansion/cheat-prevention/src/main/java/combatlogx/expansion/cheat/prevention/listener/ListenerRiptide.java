package combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IItemConfiguration;
import org.jetbrains.annotations.NotNull;

public final class ListenerRiptide extends CheatPreventionListener {
    public ListenerRiptide(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (isInCombat(player) && player.isRiptiding() && isPreventRiptide()) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.no-riptide");
        }
    }

    private @NotNull IItemConfiguration getItemConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getItemConfiguration();
    }

    private boolean isPreventRiptide() {
        IItemConfiguration itemConfiguration = getItemConfiguration();
        return itemConfiguration.isPreventRiptide();
    }
}
