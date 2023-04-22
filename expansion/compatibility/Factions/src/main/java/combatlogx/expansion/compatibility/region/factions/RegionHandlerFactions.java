package combatlogx.expansion.compatibility.region.factions;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.factions.FactionWrapper;
import com.github.sirblobman.api.factions.FactionsHandler;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

public final class RegionHandlerFactions extends RegionHandler<FactionsExpansion> {
    private final FactionsHandler factionsHandler;

    public RegionHandlerFactions(@NotNull FactionsExpansion expansion) {
        super(expansion);
        this.factionsHandler = expansion.getFactionsHandler();
    }

    private @NotNull FactionsHandler getFactionsHandler() {
        return this.factionsHandler;
    }

    @Override
    public @NotNull String getEntryDeniedMessagePath(@NotNull TagType tagType) {
        return "expansion.region-protection.factions-no-entry";
    }

    @Override
    public boolean isSafeZone(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        FactionsHandler factionsHandler = getFactionsHandler();
        FactionWrapper faction = factionsHandler.getFactionAt(location);
        if (faction == null) {
            return false;
        }

        return faction.isSafeZone();
    }
}
