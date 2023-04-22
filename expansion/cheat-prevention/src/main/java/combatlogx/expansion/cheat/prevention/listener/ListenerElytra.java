package combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityToggleGlideEvent;

import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IItemConfiguration;
import org.jetbrains.annotations.NotNull;

public final class ListenerElytra extends CheatPreventionListener {
    public ListenerElytra(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        if (player.isGliding() && isForcePreventElytra()) {
            player.setGliding(false);
            sendMessage(player, "expansion.cheat-prevention.elytra.force-disabled");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onToggle(EntityToggleGlideEvent e) {
        if (!e.isGliding()) {
            return;
        }

        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        if (isInCombat(player) && isPreventElytra()) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.elytra.no-gliding");
        }
    }

    private @NotNull IItemConfiguration getItemConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getItemConfiguration();
    }

    private boolean isPreventElytra() {
        IItemConfiguration itemConfiguration = getItemConfiguration();
        return itemConfiguration.isPreventElytra();
    }

    private boolean isForcePreventElytra() {
        IItemConfiguration itemConfiguration = getItemConfiguration();
        return itemConfiguration.isForcePreventElytra();
    }
}
