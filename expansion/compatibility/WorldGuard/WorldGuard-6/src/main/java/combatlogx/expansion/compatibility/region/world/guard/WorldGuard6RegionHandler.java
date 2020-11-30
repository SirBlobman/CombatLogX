package combatlogx.expansion.compatibility.region.world.guard;

import com.SirBlobman.combatlogx.api.expansion.region.RegionHandler;
import com.SirBlobman.combatlogx.api.object.TagType;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;

public final class WorldGuard6RegionHandler extends RegionHandler {
    public WorldGuard6RegionHandler(WorldGuard6Expansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.worldguard-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        if(tagType == TagType.UNKNOWN) return false;
        StateFlag stateFlag = (tagType == TagType.PLAYER ? HookWorldGuard6.PLAYER_COMBAT : HookWorldGuard6.MOB_COMBAT);

        ApplicableRegionSet regions = getRegions(location);
        State state = regions.queryState(null, stateFlag);
        return (state == State.DENY);
    }

    private ApplicableRegionSet getRegions(Location location) {
        World world = location.getWorld();
        WorldGuardPlugin plugin = JavaPlugin.getPlugin(WorldGuardPlugin.class);
        RegionManager regionManager = plugin.getRegionManager(world);
        return regionManager.getApplicableRegions(location);
    }
}