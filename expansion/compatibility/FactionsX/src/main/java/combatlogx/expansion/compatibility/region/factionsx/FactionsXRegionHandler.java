package combatlogx.expansion.compatibility.region.factionsx;

import com.SirBlobman.combatlogx.api.expansion.region.RegionHandler;
import com.SirBlobman.combatlogx.api.object.TagType;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
        return (tagType == TagType.PLAYER && GridManager.INSTANCE.getFactionAt(location.getChunk()).isSafezone());
    }
}