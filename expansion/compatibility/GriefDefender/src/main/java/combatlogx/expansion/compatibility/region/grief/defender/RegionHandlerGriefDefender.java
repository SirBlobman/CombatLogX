package combatlogx.expansion.compatibility.region.grief.defender;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

public final class RegionHandlerGriefDefender extends RegionHandler<GriefDefenderExpansion> {
    public RegionHandlerGriefDefender(@NotNull GriefDefenderExpansion expansion) {
        super(expansion);
    }

    @Override
    public @NotNull String getEntryDeniedMessagePath(@NotNull TagType tagType) {
        return "expansion.region-protection.griefdefender-no-entry";
    }

    @Override
    public boolean isSafeZone(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        TagType tagType = tag.getCurrentTagType();
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

    private @NotNull Core getCore() {
        return GriefDefender.getCore();
    }

    private @Nullable Claim getClaimAt(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return null;
        }

        Core core = getCore();
        return core.getClaimAt(location);
    }

    private @Nullable User getUser(Player player) {
        Core core = getCore();
        UUID playerId = player.getUniqueId();
        return core.getUser(playerId);
    }
}
