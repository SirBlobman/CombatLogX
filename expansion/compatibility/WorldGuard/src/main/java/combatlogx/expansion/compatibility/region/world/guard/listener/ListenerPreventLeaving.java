package combatlogx.expansion.compatibility.region.world.guard.listener;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.sirblobman.combatlogx.api.expansion.region.listener.RegionExpansionListener;

import combatlogx.expansion.compatibility.region.world.guard.WorldGuardExpansion;
import combatlogx.expansion.compatibility.region.world.guard.hook.HookWorldGuard;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;

public final class ListenerPreventLeaving extends RegionExpansionListener {
    private final WorldGuardExpansion expansion;

    public ListenerPreventLeaving(@NotNull WorldGuardExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!isInCombat(player)) {
            return;
        }

        Location from = e.getFrom();
        Location to = e.getTo();
        if (isPreventLeaving(player, from, to)) {
            e.setCancelled(true);
        }
    }

    private @NotNull WorldGuardExpansion getWorldGuardExpansion() {
        return this.expansion;
    }

    private @NotNull Optional<String> getPreventLeavingId(@NotNull Player player, @NotNull Location location) {
        WorldGuardExpansion worldGuardExpansion = getWorldGuardExpansion();
        HookWorldGuard hookWorldGuard = worldGuardExpansion.getHookWorldGuard();
        IWrappedFlag<String> preventLeavingFlag = hookWorldGuard.getPreventLeavingFlag();
        if (preventLeavingFlag == null) {
            return Optional.empty();
        }

        WorldGuardWrapper wrappedWorldGuard = WorldGuardWrapper.getInstance();
        return wrappedWorldGuard.queryFlag(player, location, preventLeavingFlag);
    }

    private boolean isPreventLeaving(@NotNull Player player, @NotNull Location from, @NotNull Location to) {
        Optional<String> fromId = getPreventLeavingId(player, from);
        if (fromId.isEmpty()) {
            return false;
        }

        Optional<String> toId = getPreventLeavingId(player, to);
        return toId.map(s -> !fromId.get().equals(s)).orElse(true);

    }
}
