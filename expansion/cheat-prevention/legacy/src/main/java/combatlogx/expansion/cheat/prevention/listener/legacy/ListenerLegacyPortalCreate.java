package combatlogx.expansion.cheat.prevention.listener.legacy;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCreatePortalEvent;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IBlockConfiguration;
import combatlogx.expansion.cheat.prevention.listener.CheatPreventionListener;
import org.jetbrains.annotations.NotNull;

public final class ListenerLegacyPortalCreate extends CheatPreventionListener {
    public ListenerLegacyPortalCreate(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPortalCreate(EntityCreatePortalEvent e) {
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
