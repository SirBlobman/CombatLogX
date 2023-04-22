package combatlogx.expansion.cheat.prevention.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IFlightConfiguration;
import org.jetbrains.annotations.NotNull;

public final class ListenerFlight extends CheatPreventionListener {
    private final Set<UUID> noFallDamageSet;

    public ListenerFlight(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
        this.noFallDamageSet = new HashSet<>();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        printDebug("Detected PlayerTagEvent...");
        Player player = e.getPlayer();

        printDebug("Checking player allow flight value...");
        checkAllowFlight(player);

        printDebug("Checking player flying value...");
        checkFlight(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onToggle(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();
        if (e.isFlying() && isPreventFlight() && isInCombat(player)) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.flight.no-flying");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        DamageCause damageCause = e.getCause();
        if (damageCause != DamageCause.FALL) {
            return;
        }

        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        UUID playerId = player.getUniqueId();
        if (isPreventFallDamage() && this.noFallDamageSet.remove(playerId)) {
            e.setCancelled(true);
        }
    }

    private @NotNull IFlightConfiguration getFlightConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getFlightConfiguration();
    }

    private boolean isPreventFlight() {
        IFlightConfiguration flightConfiguration = getFlightConfiguration();
        return flightConfiguration.isPreventFlying();
    }

    private boolean isForceDisableFlight() {
        IFlightConfiguration flightConfiguration = getFlightConfiguration();
        return flightConfiguration.isForceDisableFlight();
    }

    private boolean isPreventFallDamage() {
        IFlightConfiguration flightConfiguration = getFlightConfiguration();
        return flightConfiguration.isPreventFallDamage();
    }

    private void checkFlight(Player player) {
        if (isPreventFlight() && player.isFlying()) {
            player.setFlying(false);

            if (isPreventFallDamage()) {
                UUID playerId = player.getUniqueId();
                this.noFallDamageSet.add(playerId);
            }

            sendMessage(player, "expansion.cheat-prevention.flight.force-disabled");
        }
    }

    private void checkAllowFlight(Player player) {
        if (player.getAllowFlight() && isForceDisableFlight()) {
            player.setAllowFlight(false);
            sendMessage(player, "expansion.cheat-prevention.flight.force-disabled");
        }
    }
}
