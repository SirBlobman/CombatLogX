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
        UUID playerId = player.getUniqueId();

        Land land = Land.getLand(location);
        if(land == null || land.isBeingInvaded() || !land.isClaimed()) return false;

        UUID claimedBy = land.getClaimedBy();
        if(claimedBy == null || !claimedBy.equals(playerId)) return false;

        KingdomPlayer claimer = land.getClaimer();
        if(claimer == null) return false;

        Kingdom kingdom = claimer.getKingdom();
        if(kingdom == null) return false;

        Set<UUID> kingdomMemberSet = kingdom.getMembers();
        return !kingdomMemberSet.contains(playerId);
    }
}