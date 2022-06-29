package combatlogx.expansion.compatibility.region.ultimateclaims;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimManager;
import com.songoda.ultimateclaims.claim.ClaimSetting;
import com.songoda.ultimateclaims.claim.ClaimSettings;

public final class UltimateClaimsRegionHandler extends RegionHandler {
    public UltimateClaimsRegionHandler(UltimateClaimsExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.region-protection.ultimateclaims-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        if (tagType != TagType.PLAYER) return false;

        UltimateClaims ultimateClaims = UltimateClaims.getInstance();
        ClaimManager claimManager = ultimateClaims.getClaimManager();

        Chunk chunk = location.getChunk();
        Claim claim = claimManager.getClaim(chunk);
        if (claim == null) return false;

        ClaimSettings claimSettings = claim.getClaimSettings();
        return !claimSettings.isEnabled(ClaimSetting.PVP);
    }
}
