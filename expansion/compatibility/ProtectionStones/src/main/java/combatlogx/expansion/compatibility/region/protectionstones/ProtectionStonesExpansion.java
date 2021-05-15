package combatlogx.expansion.compatibility.region.protectionstones;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import combatlogx.expansion.compatibility.region.protectionstones.handler.ProtectionStonesRegionHandler;

import java.util.logging.Level;

public final class ProtectionStonesExpansion extends RegionExpansion {
    private RegionHandler regionHandler = null;

    public ProtectionStonesExpansion(final ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void afterEnable() {
        new ProtectionStonesListener(this).register();
        this.regionHandler = new ProtectionStonesRegionHandler(this);
    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("ProtectionStones", true);
    }

    @Override
    public RegionHandler getRegionHandler() {
        return this.regionHandler;
    }
}