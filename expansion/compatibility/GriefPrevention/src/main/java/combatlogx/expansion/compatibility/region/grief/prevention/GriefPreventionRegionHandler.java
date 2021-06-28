package combatlogx.expansion.compatibility.region.grief.prevention;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public final class GriefPreventionRegionHandler extends RegionHandler {
    public GriefPreventionRegionHandler(GriefPreventionExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.griefprevention-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        if(tagType == TagType.UNKNOWN) return false;

        GriefPrevention griefPrevention = JavaPlugin.getPlugin(GriefPrevention.class);
        Claim claim = griefPrevention.dataStore.getClaimAt(location, false, null);
        return (claim != null && griefPrevention.claimIsPvPSafeZone(claim));
    }
}
