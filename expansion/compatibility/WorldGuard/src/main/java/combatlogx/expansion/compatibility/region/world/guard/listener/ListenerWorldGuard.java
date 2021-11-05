package combatlogx.expansion.compatibility.region.world.guard.listener;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.compatibility.region.world.guard.WorldGuardExpansion;
import combatlogx.expansion.compatibility.region.world.guard.hook.HookWorldGuard;
import org.codemc.worldguardwrapper.WorldGuardWrapper;

public final class ListenerWorldGuard extends ExpansionListener {
    public ListenerWorldGuard(WorldGuardExpansion expansion) {
        super(expansion);
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeCombat(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();
        if(isNoTaggingRegion(player, location)) {
            e.setCancelled(true);
        }
    }
    
    private boolean isNoTaggingRegion(Player player, Location location) {
        if(player == null || location == null) return false;
        if(HookWorldGuard.NO_TAGGING == null) return false;
        
        WorldGuardWrapper instance = WorldGuardWrapper.getInstance();
        Optional<Boolean> optionalFlag = instance.queryFlag(player, location, HookWorldGuard.NO_TAGGING);
        return optionalFlag.orElse(false);
    }
}
