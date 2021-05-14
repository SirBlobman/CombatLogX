package combatlogx.expansion.compatibility.region.protectionstones;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import java.util.logging.Level;

public final class ProtectionStonesExpansion extends Expansion {

    public ProtectionStonesExpansion(final ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        new ProtectionStonesListener(this).register();
        if(!getPlugin().getExpansionManager().getExpansion("CompatWorldGuard").isPresent()) {
            getLogger().log(Level.WARNING, "ProtectionStones is a WorldGuard based plugin");
            getLogger().log(Level.WARNING, "and to enable the ForceField and other region protection features");
            getLogger().log(Level.WARNING, "please install the WorldGuard Compatibility expansion");
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void reloadConfig() {

    }
}