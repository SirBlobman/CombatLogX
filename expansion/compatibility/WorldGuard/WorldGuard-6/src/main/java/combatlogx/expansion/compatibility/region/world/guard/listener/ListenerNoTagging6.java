package combatlogx.expansion.compatibility.region.world.guard.listener;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.expansion.ExpansionListener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import combatlogx.expansion.compatibility.region.world.guard.HookWorldGuard6;
import combatlogx.expansion.compatibility.region.world.guard.WorldGuard6Expansion;

public final class ListenerNoTagging6 extends ExpansionListener {
    public ListenerNoTagging6(WorldGuard6Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();
        if(isNoTaggingArea(location)) e.setCancelled(true);
    }

    private ApplicableRegionSet getRegions(Location location) {
        World world = location.getWorld();
        WorldGuardPlugin plugin = JavaPlugin.getPlugin(WorldGuardPlugin.class);
        RegionManager regionManager = plugin.getRegionManager(world);
        return regionManager.getApplicableRegions(location);
    }

    private boolean isNoTaggingArea(Location location) {
        ApplicableRegionSet regionSet = getRegions(location);
        Boolean value = regionSet.queryValue(null, HookWorldGuard6.NO_TAGGING);
        return (value != null && value);
    }
}