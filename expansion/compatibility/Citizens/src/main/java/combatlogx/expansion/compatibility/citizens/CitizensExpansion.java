package combatlogx.expansion.compatibility.citizens;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.compatibility.citizens.configuration.CitizensConfiguration;
import combatlogx.expansion.compatibility.citizens.configuration.Configuration;
import combatlogx.expansion.compatibility.citizens.configuration.SentinelConfiguration;
import combatlogx.expansion.compatibility.citizens.listener.ListenerCombat;
import combatlogx.expansion.compatibility.citizens.listener.ListenerConvert;
import combatlogx.expansion.compatibility.citizens.listener.ListenerDeath;
import combatlogx.expansion.compatibility.citizens.listener.ListenerJoin;
import combatlogx.expansion.compatibility.citizens.listener.ListenerPunish;
import combatlogx.expansion.compatibility.citizens.listener.ListenerQuit;
import combatlogx.expansion.compatibility.citizens.listener.ListenerRespawn;
import combatlogx.expansion.compatibility.citizens.listener.ListenerResurrect;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;
import combatlogx.expansion.compatibility.citizens.manager.InventoryManager;

public final class CitizensExpansion extends Expansion {
    /**
     * The oldest Citizens release this expansion was written against. Newer releases are accepted,
     * so a hardcoded list does not have to be updated every time Citizens publishes a build.
     */
    private static final int[] MINIMUM_CITIZENS_VERSION = {2, 0, 35};

    private final Configuration configuration;
    private final CitizensConfiguration citizensConfiguration;
    private final Map<String, CitizensConfiguration> worldConfigurationMap;
    private final SentinelConfiguration sentinelConfiguration;
    private final CombatNpcManager combatNpcManager;
    private final InventoryManager inventoryManager;

    public CitizensExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.configuration = new Configuration();
        this.citizensConfiguration = new CitizensConfiguration(this);
        this.worldConfigurationMap = new HashMap<>();
        this.sentinelConfiguration = new SentinelConfiguration(this);

        this.combatNpcManager = new CombatNpcManager(this);
        this.inventoryManager = new InventoryManager(this);
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
        configurationManager.saveDefault("citizens.yml");
        configurationManager.saveDefault("sentinel.yml");
    }

    @Override
    public void onEnable() {
        if (!checkDependency("Citizens", true)) {
            selfDisable();
            return;
        }

        Logger logger = getLogger();
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin citizens = pluginManager.getPlugin("Citizens");
        if (citizens == null) {
            logger.info("Dependency 'Citizens' not enabled.");
            selfDisable();
            return;
        }

        String citizensVersion = citizens.getDescription().getVersion();
        if (!isCitizensSupported(citizensVersion)) {
            logger.info("Dependency 'Citizens' is not the correct version.");
            logger.info("Expected 2.0.35 or newer but found '" + citizensVersion + "'.");
            selfDisable();
            return;
        }

        reloadConfig();
        registerListeners();
    }

    @Override
    public void onDisable() {
        CombatNpcManager combatNpcManager = getCombatNpcManager();
        combatNpcManager.removeAll();
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
        configurationManager.reload("citizens.yml");
        configurationManager.reload("sentinel.yml");

        getConfiguration().load(configurationManager.get("config.yml"));
        loadCitizensConfiguration(configurationManager.get("citizens.yml"));
        getSentinelConfiguration().load(configurationManager.get("sentinel.yml"));
    }

    private void loadCitizensConfiguration(@NotNull YamlConfiguration config) {
        CitizensConfiguration globalConfiguration = getCitizensConfiguration();
        globalConfiguration.resetToDefaults();
        globalConfiguration.load(config);

        Map<String, CitizensConfiguration> worldMap = this.worldConfigurationMap;
        worldMap.clear();

        ConfigurationSection worldsSection = config.getConfigurationSection("worlds");
        if (worldsSection == null) {
            return;
        }

        Logger logger = getLogger();
        Set<String> worldNameSet = worldsSection.getKeys(false);
        for (String worldName : worldNameSet) {
            ConfigurationSection worldSection = worldsSection.getConfigurationSection(worldName);
            if (worldSection == null) {
                logger.warning("Ignoring 'worlds." + worldName + "' in citizens.yml: it must be a section.");
                continue;
            }

            CitizensConfiguration worldConfiguration = globalConfiguration.copy();
            worldConfiguration.load(worldSection);
            worldMap.put(worldName, worldConfiguration);
            logger.info("Loaded per-world Citizens settings for world '" + worldName + "'.");
        }
    }

    public @NotNull CombatNpcManager getCombatNpcManager() {
        return this.combatNpcManager;
    }

    public @NotNull InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public boolean isSentinelEnabled() {
        Configuration configuration = getConfiguration();
        SentinelConfiguration sentinelConfiguration = getSentinelConfiguration();
        return (configuration.isEnableSentinel() && sentinelConfiguration.isSentinelPluginEnabled());
    }

    private void registerListeners() {
        new ListenerCombat(this).register();
        new ListenerDeath(this).register();
        new ListenerJoin(this).register();
        new ListenerPunish(this).register();
        new ListenerQuit(this).register();
        new ListenerRespawn(this).register();

        int majorVersion = VersionUtility.getMajorVersion();
        int minorVersion = VersionUtility.getMinorVersion();

        // Totem of Undying was added in 1.11.
        if (majorVersion > 1 || (majorVersion == 1 && minorVersion >= 11)) {
            new ListenerResurrect(this).register();
        }

        // EntityTransformEvent was added in 1.13
        if (majorVersion > 1 || (majorVersion == 1 && minorVersion >= 13)) {
            new ListenerConvert(this).register();
        }
    }

    /**
     * @param version The Citizens plugin version, e.g. "2.0.44-SNAPSHOT (build 3870)".
     * @return {@code true} if the version is at least {@link #MINIMUM_CITIZENS_VERSION}.
     */
    private boolean isCitizensSupported(@NotNull String version) {
        Matcher matcher = Pattern.compile("^\\D*(\\d+)\\.(\\d+)\\.(\\d+)").matcher(version);
        if (!matcher.find()) {
            Logger logger = getLogger();
            logger.warning("Could not parse Citizens version '" + version + "', assuming it is supported.");
            return true;
        }

        for (int i = 0; i < MINIMUM_CITIZENS_VERSION.length; i++) {
            int actual = Integer.parseInt(matcher.group(i + 1));
            int minimum = MINIMUM_CITIZENS_VERSION[i];
            if (actual != minimum) {
                return (actual > minimum);
            }
        }

        return true;
    }

    public @NotNull Configuration getConfiguration() {
        return this.configuration;
    }

    /**
     * @return The global settings from 'citizens.yml', without any per-world overrides applied.
     */
    public @NotNull CitizensConfiguration getCitizensConfiguration() {
        return this.citizensConfiguration;
    }

    /**
     * @param world The world to get settings for. {@code null} returns the global settings.
     * @return The settings for that world: the global settings with the matching 'worlds.<name>'
     * section from 'citizens.yml' applied on top, or the global settings if the world has no section.
     */
    public @NotNull CitizensConfiguration getCitizensConfiguration(@Nullable World world) {
        if (world == null) {
            return getCitizensConfiguration();
        }

        String worldName = world.getName();
        CitizensConfiguration worldConfiguration = this.worldConfigurationMap.get(worldName);
        return (worldConfiguration == null ? getCitizensConfiguration() : worldConfiguration);
    }

    /**
     * @param location The location to get settings for. {@code null} or a location without a world
     *                 returns the global settings.
     * @return The settings for the world of that location.
     */
    public @NotNull CitizensConfiguration getCitizensConfiguration(@Nullable Location location) {
        World world = (location == null ? null : location.getWorld());
        return getCitizensConfiguration(world);
    }

    public @NotNull SentinelConfiguration getSentinelConfiguration() {
        return this.sentinelConfiguration;
    }
}
