package combatlogx.expansion.compatibility.region.towny;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

import combatlogx.expansion.compatibility.region.towny.listener.ListenerPrison;

public final class TownyExpansion extends RegionExpansion {
    private final TownyConfiguration configuration;
    private RegionHandler<?> regionHandler;

    public TownyExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.configuration = new TownyConfiguration();
        this.regionHandler = null;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("towny.yml");
    }

    @Override
    public boolean checkDependencies() {
        if (!checkDependency("Towny", true)) {
            return false;
        }

        Logger logger = getLogger();
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin plugin = pluginManager.getPlugin("Towny");
        if (plugin == null) {
            return false;
        }

        PluginDescriptionFile description = plugin.getDescription();
        String version = description.getVersion();
        int minorVersion = parseTownyMinorVersion(version);
        if (minorVersion >= 100) {
            return true;
        }

        logger.info("Dependency 'Towny' is not the correct version!");
        logger.info("Expected version > 100 but found '" + minorVersion + "'.");
        return false;
    }

    private int parseTownyMinorVersion(@NotNull String version) {
        Pattern pattern = Pattern.compile("^\\D*(\\d+)\\.(\\d+)");
        Matcher matcher = pattern.matcher(version);
        Logger logger = getLogger();

        if (!matcher.find()) {
            logger.info("Failed to find expected Towny version pattern.");
            logger.info("Expected a version with at least two numeric segments such as '0.100' but got '" + version + "'.");
            return 0;
        }

        String minorString = matcher.group(2);
        try {
            return Integer.parseInt(minorString);
        } catch (NumberFormatException ex) {
            getLogger().info("Failed to parse towny version: Expected number but got '" + minorString + "'");
            return 0;
        }
    }

    @Override
    public void afterEnable() {
        checkDependency("FlagWar", true);
        new ListenerPrison(this).register();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("towny.yml");
        getTownyConfiguration().load(configurationManager.get("towny.yml"));
    }

    @Override
    public @NotNull RegionHandler<?> getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new RegionHandlerTowny(this);
        }

        return this.regionHandler;
    }

    public @NotNull TownyConfiguration getTownyConfiguration() {
        return this.configuration;
    }
}
