package combatlogx.expansion.cheat.prevention.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class ListenerFlight extends CheatPreventionListener {
    private final Set<UUID> noFallDamageSet;

    public ListenerFlight(Expansion expansion) {
        super(expansion);
        this.noFallDamageSet = new HashSet<>();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        printDebug("Detected PlayerTagEvent...");
        Player player = e.getPlayer();

        printDebug("Checking player flying value...");
        checkFlight(player);

        printDebug("Checking player allow flight value...");
        checkAllowFlight(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onToggle(PlayerToggleFlightEvent e) {
        printDebug("Detected PlayerToggleFlightEvent...");

        if (!e.isFlying()) {
            printDebug("Event is disabling flight, ignoring.");
            return;
        }

        if (isAllowFlight()) {
            printDebug("Flight is allowed by the configuration, ignoring.");
            return;
        }

        Player player = e.getPlayer();
        if (!isInCombat(player)) {
            printDebug("Player is not in combat, ignoring.");
            return;
        }

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.flight.no-flying", null);
        printDebug("Cancelled toggle flight event and send message to player.");
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
        if (shouldPreventFallDamageOnce() && this.noFallDamageSet.contains(playerId)) {
            this.noFallDamageSet.remove(playerId);
            e.setCancelled(true);
        }
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("flight.yml");
    }

    private boolean isAllowFlight() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("prevent-flying");
    }

    private boolean isForceDisableFlight() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("force-disable-flight");
    }

    private boolean shouldPreventFallDamageOnce() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("prevent-fall-damage");
    }

    private void checkFlight(Player player) {
        if (!player.isFlying()) {
            printDebug("Player is not flying, ignoring.");
            return;
        }

        if (isAllowFlight()) {
            printDebug("Flight is allowed by the configuration, ignoring.");
            return;
        }

        player.setFlying(false);
        printDebug("Disabled player flight.");

        if (shouldPreventFallDamageOnce()) {
            UUID playerId = player.getUniqueId();
            this.noFallDamageSet.add(playerId);
        }

        sendMessage(player, "expansion.cheat-prevention.flight.force-disabled", null);
    }

    private void checkAllowFlight(Player player) {
        if (!player.getAllowFlight()) {
            printDebug("Player flight is not allowed, ignoring.");
            return;
        }

        if (!isForceDisableFlight()) {
            printDebug("Force disable flight is not enabled in the configuration, ignoring.");
            return;
        }

        player.setAllowFlight(false);
        sendMessage(player, "expansion.cheat-prevention.flight.force-disabled", null);
        printDebug("Disabled player allow flight value.");
    }
}
