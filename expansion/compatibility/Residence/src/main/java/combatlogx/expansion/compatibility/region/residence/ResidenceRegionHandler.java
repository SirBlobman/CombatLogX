package combatlogx.expansion.compatibility.region.residence;

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

public final class ResidenceRegionHandler extends RegionHandler {
    public ResidenceRegionHandler(ResidenceExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.region-protection.residence-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagInformation tagInformation) {
        TagType tagType = tagInformation.getCurrentTagType();
        if (tagType != TagType.PLAYER) {
            return false;
        }

        ResidenceInterface residenceManager = ResidenceApi.getResidenceManager();
        ClaimedResidence claimedResidence = residenceManager.getByLoc(location);
        if (claimedResidence == null) {
            return false;
        }

        ResidencePermissions residencePermissions = claimedResidence.getPermissions();
        return !residencePermissions.has(Flags.pvp, true);
    }
}
