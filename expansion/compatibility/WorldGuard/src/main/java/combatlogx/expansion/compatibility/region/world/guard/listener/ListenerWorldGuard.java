package combatlogx.expansion.compatibility.region.world.guard.listener;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

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
import org.codemc.worldguardwrapper.flag.IWrappedFlag;

public final class ListenerWorldGuard extends ExpansionListener {
    private final WorldGuardExpansion expansion;
    private final Set<UUID> preventTeleportLoop;

    public ListenerWorldGuard(@NotNull WorldGuardExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
        this.preventTeleportLoop = new HashSet<>();
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
        UUID playerId = player.getUniqueId();
        if (!this.preventTeleportLoop.remove(playerId) && e.isCancelled() && isInCombat(player)) {
            this.preventTeleportLoop.add(playerId);
            Location location = player.getLocation();
            player.teleport(location);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID playerId = player.getUniqueId();
        this.preventTeleportLoop.remove(playerId);
    }

    private @NotNull WorldGuardExpansion getWorldGuardExpansion() {
        return this.expansion;
    }

    private boolean isNoTaggingRegion(@NotNull Player player, @NotNull Location location) {
        WorldGuardExpansion expansion = getWorldGuardExpansion();
        HookWorldGuard hook = expansion.getHookWorldGuard();
        IWrappedFlag<Boolean> noTaggingFlag = hook.getNoTaggingFlag();
        if (noTaggingFlag == null) {
            return false;
        }

        WorldGuardWrapper instance = WorldGuardWrapper.getInstance();
        Optional<Boolean> optionalFlag = instance.queryFlag(player, location, noTaggingFlag);
        return optionalFlag.orElse(false);
    }
}
