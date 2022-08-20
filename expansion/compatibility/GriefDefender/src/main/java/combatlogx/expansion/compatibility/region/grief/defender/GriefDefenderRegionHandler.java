package combatlogx.expansion.compatibility.region.grief.defender;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.User;
import com.griefdefender.api.claim.Claim;

public final class GriefDefenderRegionHandler extends RegionHandler {
    public GriefDefenderRegionHandler(GriefDefenderExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.region-protection.griefdefender-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagInformation tagInformation) {
        final TagType tagType = tagInformation.getCurrentTagType();
        if (tagType != TagType.PLAYER) {
            return false;
        }

        final Claim claim = getClaimAt(location);
        if (claim == null) {
            return false;
        }

        final UUID playerId = player.getUniqueId();
        final User user = GriefDefender.getCore().getUser(playerId);
        if (user == null) {
            return false;
        }

        return !user.canPvp(claim);
    }

    private Claim getClaimAt(Location location) {
        final World world = location.getWorld();
        if (world == null) {
            return null;
        }

        return GriefDefender.getCore().getClaimAt(location);
    }
}
