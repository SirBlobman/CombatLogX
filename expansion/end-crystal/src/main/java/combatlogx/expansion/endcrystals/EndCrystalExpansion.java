package combatlogx.expansion.endcrystals;

import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

public final class EndCrystalExpansion extends Expansion {
    public EndCrystalExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Empty Method
    }

    @Override
    public void onEnable() {
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 9) {
            Logger logger = getLogger();
            logger.warning("This expansion requires Spigot 1.9.4 or higher.");
            selfDisable();
            return;
        }

        reloadConfig();
        getPreferredListener().register();
    }

    @Override
    public void onDisable() {
        // Empty Method
    }

    @Override
    public void reloadConfig() {
        // Empty Method
    }

    private @NotNull ExpansionListener getPreferredListener() {
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 13) {
            return new ListenerCrystals_Legacy(this);
        }

        String minecraftVersion = VersionUtility.getMinecraftVersion();
        if (minorVersion > 20 || minecraftVersion.equals("1.20.5") || minecraftVersion.equals("1.20.6")) {
            return new ListenerCrystals_Moderner(this);
        }

        return new ListenerCrystals_Modern(this);
    }
}
