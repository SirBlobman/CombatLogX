package combatlogx.expansion.compatibility.region.factions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.factions.FactionsHandler;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

public final class FactionsRegionHandler extends RegionHandler {
    private final FactionsHandler factionsHandler;
    
    public FactionsRegionHandler(FactionsExpansion expansion) {
        super(expansion);
        this.factionsHandler = expansion.getFactionsHandler();
    }
    
    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.region-protection.factions-no-entry";
    }
    
    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        FactionsHandler factionsHandler = getFactionsHandler();
        return factionsHandler.isSafeZone(location);
    }
    
    private FactionsHandler getFactionsHandler() {
        return this.factionsHandler;
    }
}
