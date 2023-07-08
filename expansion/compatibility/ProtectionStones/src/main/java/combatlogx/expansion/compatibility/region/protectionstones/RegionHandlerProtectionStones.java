package combatlogx.expansion.compatibility.region.protectionstones;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.espi.protectionstones.PSRegion;

public final class RegionHandlerProtectionStones extends RegionHandler<ProtectionStonesExpansion> {
    public RegionHandlerProtectionStones(@NotNull ProtectionStonesExpansion expansion) {
        super(expansion);
    }

    @Override
    public @NotNull String getEntryDeniedMessagePath(@NotNull TagType tagType) {
        return "expansion.region-protection.protectionstones.no-entry";
    }

    @Override
    public boolean isSafeZone(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        TagType tagType = tag.getCurrentTagType();
        if (tagType != TagType.PLAYER) {
            return false;
        }

        ProtectedRegion region = getWorldGuardRegion(location);
        if (region == null) {
            return false;
        }

        State pvpState = region.getFlag(Flags.PVP);
        return (pvpState == State.DENY);
    }

    private @Nullable PSRegion getRegion(@NotNull Location location) {
        return PSRegion.fromLocation(location);
    }

    private @Nullable ProtectedRegion getWorldGuardRegion(@NotNull Location location) {
        PSRegion region = getRegion(location);
        if (region == null) {
            return null;
        }

        return region.getWGRegion();
    }
}
