package combatlogx.expansion.compatibility.region.husktowns;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import me.william278.husktowns.HuskTownsAPI;
import me.william278.husktowns.listener.ActionType;

public final class HuskTownsRegionHandler extends RegionHandler {
    public HuskTownsRegionHandler(HuskTownsExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.region-protection.husktowns-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        HuskTownsAPI api = HuskTownsAPI.getInstance();
        return switch(tagType) {
            case PLAYER -> !api.isActionAllowed(location, ActionType.PVP);
            case MOB -> !api.isActionAllowed(location, ActionType.PVE);
            default -> false;
        };
    }
}
