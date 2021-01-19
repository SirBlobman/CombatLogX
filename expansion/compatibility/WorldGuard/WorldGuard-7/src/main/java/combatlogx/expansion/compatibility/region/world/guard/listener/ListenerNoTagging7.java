package combatlogx.expansion.compatibility.region.world.guard.listener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import combatlogx.expansion.compatibility.region.world.guard.HookWorldGuard7;
import combatlogx.expansion.compatibility.region.world.guard.WorldGuard7Expansion;

public final class ListenerNoTagging7 extends ExpansionListener {
    public ListenerNoTagging7(WorldGuard7Expansion expansion) {
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
        Validate.notNull(world, "location must not have a null world!");
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);

        WorldGuard api = WorldGuard.getInstance();
        WorldGuardPlatform platform = api.getPlatform();
        RegionContainer regionContainer = platform.getRegionContainer();

        RegionManager regionManager = regionContainer.get(weWorld);
        Validate.notNull(regionManager, "regionManager must not be null!");

        BlockVector3 vector3 = BukkitAdapter.asBlockVector(location);
        return regionManager.getApplicableRegions(vector3);
    }

    private boolean isNoTaggingArea(Location location) {
        ApplicableRegionSet regionSet = getRegions(location);
        Boolean value = regionSet.queryValue(null, HookWorldGuard7.NO_TAGGING);
        return (value != null && value);
    }
}