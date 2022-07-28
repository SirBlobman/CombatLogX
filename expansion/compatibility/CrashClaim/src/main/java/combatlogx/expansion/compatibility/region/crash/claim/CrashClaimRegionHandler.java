package combatlogx.expansion.compatibility.region.crash.claim;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import net.crashcraft.crashclaim.CrashClaim;
import net.crashcraft.crashclaim.api.CrashClaimAPI;
import net.crashcraft.crashclaim.claimobjects.Claim;

public final class CrashClaimRegionHandler extends RegionHandler {
    public CrashClaimRegionHandler(CrashClaimExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.region-protection.crashclaim.no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagInformation tagInformation) {
        CrashClaim crashClaim = JavaPlugin.getPlugin(CrashClaim.class);
        CrashClaimAPI api = crashClaim.getApi();
        Claim claim = api.getClaim(location);
        return (claim != null);
    }
}
