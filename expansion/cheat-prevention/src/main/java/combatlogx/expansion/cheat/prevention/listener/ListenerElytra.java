package combatlogx.expansion.cheat.prevention.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IItemConfiguration;

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
        if (!(entity instanceof Player player)) {
            return;
        }

        if (isInCombat(player) && isPreventElytra()) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.elytra.no-gliding");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLaunch(ProjectileLaunchEvent e) {
        Projectile projectile = e.getEntity();
        if (projectile instanceof Firework firework) {
            ProjectileSource shooter = firework.getShooter();
            if (shooter instanceof Player player && isInCombat(player)) {
                e.setCancelled(true);
                sendMessage(player, "expansion.cheat-prevention.elytra.no-fireworks");
            }
        }
    }

    private @NotNull IItemConfiguration getItemConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getItemConfiguration();
    }

    private boolean isPreventElytra() {
        return getItemConfiguration().isPreventElytra();
    }

    private boolean isForcePreventElytra() {
        return getItemConfiguration().isForcePreventElytra();
    }

    private boolean isPreventFireworks() {
        return getItemConfiguration().isPreventFireworks();
    }
}
