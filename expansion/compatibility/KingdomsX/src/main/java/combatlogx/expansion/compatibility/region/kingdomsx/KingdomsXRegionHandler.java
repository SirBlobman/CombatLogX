package combatlogx.expansion.compatibility.region.kingdomsx;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.land.Land;

public final class KingdomsXRegionHandler extends RegionHandler {
    public KingdomsXRegionHandler(KingdomsXExpansion expansion) {
        super(expansion);
    }
    
    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.region-protection.kingdomsx-no-entry";
    }
    
    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        if(tagType != TagType.PLAYER) {
            return false;
        }
        
        Land land = Land.getLand(location);
        if(land == null || !land.isClaimed() || land.isBeingInvaded()) {
            return false;
        }
        
        Kingdom kingdom = land.getKingdom();
        if(kingdom == null) {
            return false;
        }
        
        UUID playerId = player.getUniqueId();
        Set<UUID> memberSet = kingdom.getMembers();
        return !memberSet.contains(playerId);
    }
}
