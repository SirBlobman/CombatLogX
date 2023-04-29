package com.github.sirblobman.combatlogx.api.expansion.region;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionWithDependencies;
import com.github.sirblobman.combatlogx.api.expansion.region.configuration.RegionExpansionConfiguration;
import com.github.sirblobman.combatlogx.api.expansion.region.listener.RegionMoveListener;
import com.github.sirblobman.combatlogx.api.expansion.region.listener.RegionVulnerableListener;

public abstract class RegionExpansion extends ExpansionWithDependencies {
    private final RegionExpansionConfiguration configuration;

    public RegionExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.configuration = new RegionExpansionConfiguration();
    }

    @Override
    public void onLoad() {
        File dataFolder = getDataFolder();
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            saveDefaultRegionConfig(configFile);
        }
    }

    @Override
    public final void onCheckedEnable() {
        reloadConfig();
        registerListeners();
        afterEnable();
    }

    @Override
    public final void onCheckedDisable() {
        afterDisable();
    }

    private void registerListeners() {
        new RegionMoveListener(this).register();
        new RegionVulnerableListener(this).register();
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
        getConfiguration().load(configurationManager.get("config.yml"));
    }

    public final @NotNull RegionExpansionConfiguration getConfiguration() {
        return this.configuration;
    }

    /**
     * You can override this method if you need to do something when the expansion is enabled.
     */
    public void afterEnable() {
        // Do Nothing
    }

    /**
     * You can override this method if you need to do something when the expansion is disabled.
     */
    public void afterDisable() {
        // Do Nothing
    }

    public abstract @NotNull RegionHandler<?> getRegionHandler();

    private void saveDefaultRegionConfig(File file) {
        ConfigurablePlugin plugin = getPlugin().getPlugin();
        ConfigurationManager pluginConfigManager = plugin.getConfigurationManager();

        try {
            String defaultConfigName = "default-region-expansion-config.yml";
            YamlConfiguration defaultConfig = pluginConfigManager.getInternal(defaultConfigName);
            if (defaultConfig == null) {
                throw new IOException("Missing file 'default-region-expansion-config.yml' in jar.");
            }

            defaultConfig.save(file);
        } catch (IOException ex) {
            Logger logger = getLogger();
            logger.log(Level.WARNING, "Failed to create the default region configuration:", ex);
        }
    }
}
