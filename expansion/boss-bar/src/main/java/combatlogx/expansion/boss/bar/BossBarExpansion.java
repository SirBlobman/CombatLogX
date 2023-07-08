package combatlogx.expansion.boss.bar;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.manager.ITimerManager;

public final class BossBarExpansion extends Expansion {
    private final BossBarConfiguration configuration;

    public BossBarExpansion(ICombatLogX plugin) {
        super(plugin);
        this.configuration = new BossBarConfiguration();
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        reloadConfig();
        ITimerManager timerManager = getTimerManager();
        timerManager.addUpdaterTask(new BossBarUpdater(this));
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");

        BossBarConfiguration configuration = getConfiguration();
        YamlConfiguration yamlConfiguration = configurationManager.get("config.yml");
        configuration.load(yamlConfiguration);
    }

    public @NotNull BossBarConfiguration getConfiguration() {
        return this.configuration;
    }

    private @NotNull ITimerManager getTimerManager() {
        ICombatLogX plugin = getPlugin();
        return plugin.getTimerManager();
    }
}
