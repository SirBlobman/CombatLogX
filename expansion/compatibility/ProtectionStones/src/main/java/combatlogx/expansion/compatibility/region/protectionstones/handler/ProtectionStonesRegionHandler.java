package combatlogx.expansion.compatibility.region.protectionstones.handler;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import dev.espi.protectionstones.PSRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtectionStonesRegionHandler extends RegionHandler {

    public ProtectionStonesRegionHandler(final RegionExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(final TagType tagType) {
        return "expansion.protectionstones-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(final Player player, final Location location, final TagType tagType) {
        PSRegion region = PSRegion.fromLocation(location);
        return region != null && region.getWGRegion().getFlag(Flags.PVP) == StateFlag.State.DENY;
    }
}
