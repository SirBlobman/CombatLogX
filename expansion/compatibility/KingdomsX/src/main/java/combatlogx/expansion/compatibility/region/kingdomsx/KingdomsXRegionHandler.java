package combatlogx.expansion.compatibility.region.kingdomsx;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import org.kingdoms.constants.kingdom.Kingdom;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.player.KingdomPlayer;

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
        if(land == null || land.isBeingInvaded() || !land.isClaimed()) return false;

        UUID claimedBy = land.getClaimedBy();
        if(claimedBy != null && claimedBy.equals(player.getUniqueId())) return false;

        KingdomPlayer claimer = land.getClaimer();
        if(claimer != null) {
            Kingdom kingdom = claimer.getKingdom();
            if(kingdom != null) {
                Set<UUID> memberSet = kingdom.getMembers();
                return !memberSet.contains(player.getUniqueId());
            }
        }

        return false;
    }
}