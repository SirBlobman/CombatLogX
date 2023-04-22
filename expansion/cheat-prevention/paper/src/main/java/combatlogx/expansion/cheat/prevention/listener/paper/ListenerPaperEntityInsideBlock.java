package combatlogx.expansion.cheat.prevention.listener.paper;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import io.papermc.paper.event.entity.EntityInsideBlockEvent;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.ITeleportConfiguration;
import combatlogx.expansion.cheat.prevention.listener.CheatPreventionListener;
import org.jetbrains.annotations.NotNull;

public final class ListenerPaperEntityInsideBlock extends CheatPreventionListener {
    public ListenerPaperEntityInsideBlock(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityInsideBlock(EntityInsideBlockEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        if (isPreventPortals() && isInCombat(player)) {
            check(player, e);
        }
    }

    private void check(@NotNull Player player, @NotNull EntityInsideBlockEvent e) {
        Block block = e.getBlock();
        Material blockType = block.getType();
        if (blockType != Material.END_PORTAL && blockType != Material.NETHER_PORTAL
                && blockType != Material.END_GATEWAY) {
            return;
        }

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.teleportation.block-portal");
    }

    private @NotNull ITeleportConfiguration getTeleportationConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getTeleportConfiguration();
    }

    private boolean isPreventPortals() {
        ITeleportConfiguration teleportationConfiguration = getTeleportationConfiguration();
        return teleportationConfiguration.isPreventPortals();
    }
}
