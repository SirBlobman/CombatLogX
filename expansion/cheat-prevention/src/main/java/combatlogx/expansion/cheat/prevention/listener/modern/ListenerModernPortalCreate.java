package combatlogx.expansion.cheat.prevention.listener.modern;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.PortalCreateEvent;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IBlockConfiguration;
import combatlogx.expansion.cheat.prevention.listener.CheatPreventionListener;

public final class ListenerModernPortalCreate extends CheatPreventionListener {
    public ListenerModernPortalCreate(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPortalCreate(PortalCreateEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        if (isPreventPortalCreation() && isInCombat(player)) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.blocks.prevent-portal-creation");
        }
    }

    private @NotNull IBlockConfiguration getBlockConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getBlockConfiguration();
    }

    private boolean isPreventPortalCreation() {
        IBlockConfiguration configuration = getBlockConfiguration();
        return configuration.isPreventPortalCreation();
    }
}
