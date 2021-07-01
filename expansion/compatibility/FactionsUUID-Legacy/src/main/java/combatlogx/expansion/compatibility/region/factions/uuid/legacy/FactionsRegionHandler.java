package combatlogx.expansion.compatibility.region.factions.uuid.legacy;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

public final class FactionsRegionHandler extends RegionHandler {
    public FactionsRegionHandler(FactionsUUIDLegacyExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.factions-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        if(tagType != TagType.PLAYER) return false;
        FLocation flocation = new FLocation(location);

        Board board = Board.getInstance();
        Faction faction = board.getFactionAt(flocation);
        if(faction == null) return false;

        return faction.isSafeZone();
    }
}
