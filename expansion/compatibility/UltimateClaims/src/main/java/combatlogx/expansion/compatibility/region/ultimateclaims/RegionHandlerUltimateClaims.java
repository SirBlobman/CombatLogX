package combatlogx.expansion.compatibility.region.ultimateclaims;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimManager;
import com.songoda.ultimateclaims.claim.ClaimSetting;
import com.songoda.ultimateclaims.claim.ClaimSettings;

public final class RegionHandlerUltimateClaims extends RegionHandler<UltimateClaimsExpansion> {
    public RegionHandlerUltimateClaims(@NotNull UltimateClaimsExpansion expansion) {
        super(expansion);
    }

    @Override
    public @NotNull String getEntryDeniedMessagePath(@NotNull TagType tagType) {
        return "expansion.region-protection.ultimateclaims-no-entry";
    }

    @Override
    public boolean isSafeZone(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        TagType tagType = tag.getCurrentTagType();
        if (tagType != TagType.PLAYER) {
            return false;
        }

        ClaimSettings claimSettings = getClaimSettings(location);
        if (claimSettings == null) {
            return false;
        }

        return !claimSettings.isEnabled(ClaimSetting.PVP);
    }

    private @NotNull UltimateClaims getUltimateClaims() {
        return UltimateClaims.getInstance();
    }

    private @NotNull ClaimManager getClaimManager() {
        UltimateClaims ultimateClaims = getUltimateClaims();
        return ultimateClaims.getClaimManager();
    }

    private @Nullable Claim getClaim(@NotNull Location location) {
        Chunk chunk = location.getChunk();
        ClaimManager claimManager = getClaimManager();
        return claimManager.getClaim(chunk);
    }

    private @Nullable ClaimSettings getClaimSettings(@NotNull Location location) {
        Claim claim = getClaim(location);
        if (claim == null) {
            return null;
        }

        return claim.getClaimSettings();
    }
}
