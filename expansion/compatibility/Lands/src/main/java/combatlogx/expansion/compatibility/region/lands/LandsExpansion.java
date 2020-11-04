package combatlogx.expansion.compatibility.region.lands;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;
import com.SirBlobman.combatlogx.api.expansion.region.RegionExpansion;
import com.SirBlobman.combatlogx.api.expansion.region.RegionHandler;

public final class LandsExpansion extends RegionExpansion {
    private RegionHandler regionHandler;
    public LandsExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public void onLoad() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("Lands", true);
    }

    @Override
    public RegionHandler getRegionHandler() {
        if(this.regionHandler == null) {
            this.regionHandler = new LandsRegionHandler(this);
        }

        return this.regionHandler;
    }

    @Override
    public void reloadConfig() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
    }
}