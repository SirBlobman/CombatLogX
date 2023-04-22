package com.github.sirblobman.combatlogx.api.expansion.vanish;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionWithDependencies;

public abstract class VanishExpansion extends ExpansionWithDependencies {
    private final VanishExpansionConfiguration configuration;

    public VanishExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.configuration = new VanishExpansionConfiguration();
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
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
        new VanishListener(this).register();
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
        getConfiguration().load(configurationManager.get("config.yml"));
    }

    public final @NotNull VanishExpansionConfiguration getConfiguration() {
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

    public abstract @NotNull VanishHandler<?> getVanishHandler();
}
