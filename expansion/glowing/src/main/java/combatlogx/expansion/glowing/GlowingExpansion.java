package combatlogx.expansion.glowing;

import java.util.Collection;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

import combatlogx.expansion.glowing.listener.ListenerGlow;

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
        if (minorVersion < 9) {
            logger.warning("This expansion is made for 1.9+");
            expansionManager.disableExpansion(this);
            return;
        }

        new ListenerGlow(this).register();
    }

    @Override
    public void onDisable() {
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 9) {
            return;
        }

        // Remove glowing for all online players.
        Collection<? extends Player> onlinePlayerCollection = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayerCollection) {
            player.setGlowing(false);
        }
    }

    @Override
    public void reloadConfig() {
        // Do Nothing
    }
}
