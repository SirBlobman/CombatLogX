package combatlogx.expansion.compatibility.region.factions;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.ps.PS;

public final class FactionsRegionHandler extends RegionHandler {
    public FactionsRegionHandler(FactionsExpansion expansion) {
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
        PS ps = PS.valueOf(chunk);

        BoardColl boardColl = BoardColl.get();
        Faction faction = boardColl.getFactionAt(ps);
        if(faction == null) return false;

        String factionId = faction.getId();
        return Factions.ID_SAFEZONE.equals(factionId);
    }
}
