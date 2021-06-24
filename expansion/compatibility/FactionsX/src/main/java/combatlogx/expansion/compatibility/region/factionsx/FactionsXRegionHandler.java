package combatlogx.expansion.compatibility.region.factionsx;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.GridManager;

public class FactionsXRegionHandler extends RegionHandler {
    public FactionsXRegionHandler(FactionsXExpansion expansion) {
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
        Faction faction = GridManager.INSTANCE.getFactionAt(chunk);
        return faction.isSafezone();
    }
}
