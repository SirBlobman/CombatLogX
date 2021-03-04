package combatlogx.expansion.compatibility.region.factions.uuid;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

public class FactionsRegionHandler extends RegionHandler {
    public FactionsRegionHandler(FactionsUUIDExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.factions-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        if(tagType != TagType.PLAYER) return false;

        Chunk chunk = location.getChunk();
        FLocation flocation = new FLocation(chunk);

        Board board = Board.getInstance();
        Faction faction = board.getFactionAt(flocation);
        if(faction == null) return false;

        return faction.isSafeZone();
    }
}