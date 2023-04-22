package combatlogx.expansion.compatibility.region.residence;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.api.ResidenceInterface;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;

public final class RegionHandlerResidence extends RegionHandler<ResidenceExpansion> {
    public RegionHandlerResidence(@NotNull ResidenceExpansion expansion) {
        super(expansion);
    }

    @Override
    public @NotNull String getEntryDeniedMessagePath(@NotNull TagType tagType) {
        return "expansion.region-protection.residence-no-entry";
    }

    @Override
    public boolean isSafeZone(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        TagType tagType = tag.getCurrentTagType();
        Flags flag = getFlag(tagType);
        if (flag == null) {
            return false;
        }

        ResidencePermissions permissions = getPermissions(location);
        if (permissions == null) {
            return false;
        }

        return !permissions.has(flag, true);
    }

    private @NotNull ResidenceInterface getInterface() {
        return ResidenceApi.getResidenceManager();
    }

    private @Nullable ClaimedResidence getResidence(@NotNull Location location) {
        ResidenceInterface manager = getInterface();
        return manager.getByLoc(location);
    }

    private @Nullable ResidencePermissions getPermissions(@NotNull Location location) {
        ClaimedResidence residence = getResidence(location);
        if (residence == null) {
            return null;
        }

        return residence.getPermissions();
    }

    private @Nullable Flags getFlag(@NotNull TagType tagType) {
        switch (tagType) {
            case PLAYER:
                return Flags.pvp;
            case MOB:
            case MYTHIC_MOB:
                return Flags.mobkilling;
            case DAMAGE:
                return Flags.damage;
            default:
                break;
        }

        return null;
    }
}
