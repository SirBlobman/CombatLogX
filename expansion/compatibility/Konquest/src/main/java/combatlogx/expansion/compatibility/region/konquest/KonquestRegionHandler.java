package combatlogx.expansion.compatibility.region.konquest;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import konquest.Konquest;
import konquest.manager.KingdomManager;
import konquest.model.KonKingdom;
import konquest.model.KonTerritory;

public class KonquestRegionHandler extends RegionHandler {
    public KonquestRegionHandler(KonquestExpansion expansion) {
        super(expansion);
    }
    
    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.region-protection.konquest-no-entry";
    }
    
    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        Konquest konquest = Konquest.getInstance();
        KingdomManager kingdomManager = konquest.getKingdomManager();
        KonTerritory chunkTerritory = kingdomManager.getChunkTerritory(location);
        if(chunkTerritory == null) {
            return false;
        }
        
        KonKingdom locationKingdom = chunkTerritory.getKingdom();
        return (locationKingdom != null);
    }
}
