package combatlogx.expansion.compatibility.region.grief.defender;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.griefdefender.api.Core;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.User;
import com.griefdefender.api.claim.Claim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        TagType tagType = tagInformation.getCurrentTagType();
        if (tagType != TagType.PLAYER) {
            return false;
        }

        Claim claim = getClaimAt(location);
        if (claim == null) {
            return false;
        }

        User user = getUser(player);
        if (user == null) {
            return false;
        }

        return !user.canPvp(claim);
    }

    @NotNull
    private Core getCore() {
        return GriefDefender.getCore();
    }

    @Nullable
    private Claim getClaimAt(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return null;
        }

        Core griefDefenderCore = getCore();
        return griefDefenderCore.getClaimAt(location);
    }

    @Nullable
    private User getUser(Player player) {
        Core griefDefenderCore = getCore();
        UUID playerId = player.getUniqueId();
        return griefDefenderCore.getUser(playerId);
    }
}
