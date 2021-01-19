package combatlogx.expansion.compatibility.region.world.guard;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public final class WorldGuard7RegionHandler extends RegionHandler {
    public WorldGuard7RegionHandler(WorldGuard7Expansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.worldguard-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        if(tagType == TagType.UNKNOWN) return false;
        StateFlag stateFlag = (tagType == TagType.PLAYER ? HookWorldGuard7.PLAYER_COMBAT : HookWorldGuard7.MOB_COMBAT);

        ApplicableRegionSet regions = getRegions(location);
        State state = regions.queryState(null, stateFlag);
        return (state == State.DENY);
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
}