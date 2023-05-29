package combatlogx.expansion.compatibility.region.konquest;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.github.rumsfield.konquest.Konquest;
import com.github.rumsfield.konquest.api.KonquestAPI;
import com.github.rumsfield.konquest.api.manager.KonquestTerritoryManager;
import com.github.rumsfield.konquest.api.model.KonquestKingdom;
import com.github.rumsfield.konquest.api.model.KonquestTerritory;

public class RegionHandlerKonquest extends RegionHandler<KonquestExpansion> {
    public RegionHandlerKonquest(@NotNull KonquestExpansion expansion) {
        super(expansion);
    }

    @Override
    public @NotNull String getEntryDeniedMessagePath(@NotNull TagType tagType) {
        return "expansion.region-protection.konquest-no-entry";
    }

    @Override
    public boolean isSafeZone(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        KonquestAPI api = getAPI();
        KonquestTerritoryManager territoryManager = api.getTerritoryManager();
        KonquestTerritory territory = territoryManager.getChunkTerritory(location);
        if (territory == null) {
            return false;
        }

        KonquestKingdom kingdom = territory.getKingdom();
        return (kingdom != null);
    }

    private @NotNull KonquestAPI getAPI() {
        return Konquest.getInstance();
    }
}
