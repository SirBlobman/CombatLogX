package combatlogx.expansion.compatibility.region.grief.prevention;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public final class RegionHandlerGriefPrevention extends RegionHandler<GriefPreventionExpansion> {
    public RegionHandlerGriefPrevention(GriefPreventionExpansion expansion) {
        super(expansion);
    }

    @Override
    public @NotNull String getEntryDeniedMessagePath(@NotNull TagType tagType) {
        return "expansion.region-protection.griefprevention-no-entry";
    }

    @Override
    public boolean isSafeZone(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        TagType tagType = tag.getCurrentTagType();
        if (tagType != TagType.PLAYER) {
            return false;
        }

        Claim claim = getClaim(location);
        GriefPrevention griefPrevention = getGriefPrevention();
        return (claim != null && griefPrevention.claimIsPvPSafeZone(claim));
    }

    private @NotNull GriefPrevention getGriefPrevention() {
        return JavaPlugin.getPlugin(GriefPrevention.class);
    }

    private @Nullable Claim getClaim(@NotNull Location location) {
        GriefPrevention griefPrevention = getGriefPrevention();
        return griefPrevention.dataStore.getClaimAt(location, false, null);
    }
}
