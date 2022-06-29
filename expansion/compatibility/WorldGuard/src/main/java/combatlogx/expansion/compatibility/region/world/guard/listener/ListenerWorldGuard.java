package combatlogx.expansion.compatibility.region.world.guard.listener;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.compatibility.region.world.guard.WorldGuardExpansion;
import combatlogx.expansion.compatibility.region.world.guard.hook.HookWorldGuard;
import org.codemc.worldguardwrapper.WorldGuardWrapper;

public final class ListenerWorldGuard extends ExpansionListener {
    private final Set<UUID> preventTeleportLoop = new HashSet<>();

    public ListenerWorldGuard(WorldGuardExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeCombat(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();
        if (isNoTaggingRegion(player, location)) {
            e.setCancelled(true);
        }
    }

    // Bug Fix: WorldGuard Invulnerability when teleport is cancelled during combat.
    // Bug Reported and Patched by PhoenixZT#0278 on Discord.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        UUID uniqueId = player.getUniqueId();
        if (!preventTeleportLoop.remove(uniqueId) && e.isCancelled() && isInCombat(player)) {
            preventTeleportLoop.add(uniqueId);
            Location location = player.getLocation();
            player.teleport(location);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uniqueId = player.getUniqueId();
        preventTeleportLoop.remove(uniqueId);
    }

    private boolean isNoTaggingRegion(Player player, Location location) {
        if (player == null || location == null) {
            return false;
        }

        if (HookWorldGuard.NO_TAGGING == null) {
            return false;
        }

        WorldGuardWrapper instance = WorldGuardWrapper.getInstance();
        Optional<Boolean> optionalFlag = instance.queryFlag(player, location, HookWorldGuard.NO_TAGGING);
        return optionalFlag.orElse(false);
    }
}
