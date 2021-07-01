package combatlogx.expansion.compatibility.region.factions.uuid;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class FactionsUUIDExpansion extends RegionExpansion {
    private RegionHandler regionHandler;

    public FactionsUUIDExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        if(!checkDependency("Factions", true)) return false;

        Logger logger = getLogger();
        logger.info("Checking if Factions plugin is the correct version...");

        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin pluginFactions = pluginManager.getPlugin("Factions");
        if(pluginFactions != null) {
            PluginDescriptionFile description = pluginFactions.getDescription();
            String version = description.getVersion();
            if(version.startsWith("1.6.9.5-U0.5")) {
                logger.info("The Factions plugin passed the check.");
                return true;
            }
        }

        logger.warning("The Factions plugin does not match the type of plugin supported by this expansion.");
        logger.warning("If you believe this is a mistake, please contact SirBlobman!");
        return false;
    }

    @Override
    public RegionHandler getRegionHandler() {
        if(this.regionHandler == null) {
            this.regionHandler = new FactionsRegionHandler(this);
        }

        return this.regionHandler;
    }
}
