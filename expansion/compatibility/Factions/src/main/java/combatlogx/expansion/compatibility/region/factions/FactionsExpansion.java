package combatlogx.expansion.compatibility.region.factions;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.factions.FactionsHandler;
import com.github.sirblobman.api.factions.FactionsHelper;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class FactionsExpansion extends RegionExpansion {
    private RegionHandler regionHandler;
    private FactionsHandler factionsHandler;

    public FactionsExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
        this.factionsHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        ICombatLogX combatLogX = getPlugin();
        JavaPlugin plugin = combatLogX.getPlugin();
        FactionsHelper factionsHelper = new FactionsHelper(plugin);

        this.factionsHandler = factionsHelper.getFactionsHandler();
        return (this.factionsHandler != null);
    }

    @Override
    public RegionHandler getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new FactionsRegionHandler(this);
        }

        return this.regionHandler;
    }

    public FactionsHandler getFactionsHandler() {
        return this.factionsHandler;
    }
}
