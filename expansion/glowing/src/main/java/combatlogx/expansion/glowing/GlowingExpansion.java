package combatlogx.expansion.glowing;

import combatlogx.expansion.glowing.listener.ListenerGlow;

import java.util.logging.Logger;

import com.SirBlobman.api.utility.VersionUtility;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;

public class GlowingExpansion extends Expansion {
    public GlowingExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        Logger logger = getLogger();
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();

        int minorVersion = VersionUtility.getMinorVersion();
        if(minorVersion < 9) {
            logger.warning("This expansion is made for 1.9+");
            expansionManager.disableExpansion(this);
            return;
        }

        new ListenerGlow(this).register();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        // Do Nothing
    }
}