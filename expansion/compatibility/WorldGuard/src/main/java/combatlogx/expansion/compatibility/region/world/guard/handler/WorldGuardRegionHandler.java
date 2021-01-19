package combatlogx.expansion.compatibility.region.world.guard.handler;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import combatlogx.expansion.compatibility.region.world.guard.WorldGuardExpansion;
import combatlogx.expansion.compatibility.region.world.guard.hook.HookWorldGuard;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;

public final class WorldGuardRegionHandler extends RegionHandler {
    public WorldGuardRegionHandler(WorldGuardExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.worldguard-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        if(tagType == TagType.UNKNOWN) return false;

        WorldGuardWrapper instance = WorldGuardWrapper.getInstance();
        IWrappedFlag<WrappedState> flag = (tagType == TagType.PLAYER ? HookWorldGuard.PLAYER_COMBAT : HookWorldGuard.MOB_COMBAT);

        Optional<WrappedState> optionalState = instance.queryFlag(player, location, flag);
        if(optionalState.isPresent()) {
            WrappedState state = optionalState.get();
            return (state == WrappedState.DENY);
        }

        return false;
    }
}