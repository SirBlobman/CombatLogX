package combatlogx.expansion.compatibility.region.kingdomsx;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import org.kingdoms.constants.land.Land;

public final class KingdomsXRegionHandler extends RegionHandler {
    public KingdomsXRegionHandler(KingdomsXExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.kingdomsx-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        if(tagType != TagType.PLAYER) return false;

        Land land = Land.getLand(location);
        return (land != null && land.isClaimed() && !land.isBeingInvaded());
    }
}
