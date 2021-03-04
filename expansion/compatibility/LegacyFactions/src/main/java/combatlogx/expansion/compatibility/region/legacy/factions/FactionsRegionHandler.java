package combatlogx.expansion.compatibility.region.legacy.factions;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Faction;

public class FactionsRegionHandler extends RegionHandler {
    public FactionsRegionHandler(LegacyFactionsExpansion expansion) {
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

        Board board = Board.get();
        Faction faction = board.getFactionAt(flocation);
        if(faction == null) return false;

        return faction.isSafeZone();
    }
}