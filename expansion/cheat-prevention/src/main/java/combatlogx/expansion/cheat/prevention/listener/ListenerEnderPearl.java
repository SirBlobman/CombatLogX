package combatlogx.expansion.cheat.prevention.listener;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.ITeleportConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

public final class ListenerEnderPearl extends CheatPreventionListener {
    public ListenerEnderPearl(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLand(ProjectileLaunchEvent e) {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        ITeleportConfiguration teleportConfiguration = expansion.getTeleportConfiguration();
        if (!teleportConfiguration.isForceDisableEnderPearl()) {
            return;
        }

        Projectile projectile = e.getEntity();
        if (!(projectile instanceof EnderPearl enderPearl)) {
            return;
        }

        ProjectileSource shooter = enderPearl.getShooter();
        if (shooter instanceof Player player && isInCombat(player)) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.teleportation.block-pearl");
        }
    }
}
