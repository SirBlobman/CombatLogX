package combatlogx.expansion.compatibility.region.crash.claim;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import net.crashcraft.crashclaim.CrashClaim;
import net.crashcraft.crashclaim.api.CrashClaimAPI;
import net.crashcraft.crashclaim.claimobjects.Claim;

public final class CrashClaimRegionHandler extends RegionHandler<CrashClaimExpansion> {
    public CrashClaimRegionHandler(@NotNull CrashClaimExpansion expansion) {
        super(expansion);
    }

    @Override
    public @NotNull String getEntryDeniedMessagePath(@NotNull TagType tagType) {
        return "expansion.region-protection.crashclaim.no-entry";
    }

    @Override
    public boolean isSafeZone(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        Claim claim = getClaim(location);
        return (claim != null);
    }

    private @NotNull CrashClaim getCrashClaim() {
        return JavaPlugin.getPlugin(CrashClaim.class);
    }

    private @NotNull CrashClaimAPI getAPI() {
        CrashClaim crashClaim = getCrashClaim();
        return crashClaim.getApi();
    }

    private @Nullable Claim getClaim(@NotNull Location location) {
        CrashClaimAPI api = getAPI();
        return api.getClaim(location);
    }
}
