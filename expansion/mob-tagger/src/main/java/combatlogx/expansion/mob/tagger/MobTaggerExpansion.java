package combatlogx.expansion.mob.tagger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.mob.tagger.configuration.MobTaggerConfiguration;
import combatlogx.expansion.mob.tagger.listener.ListenerDamage;
import combatlogx.expansion.mob.tagger.manager.ISpawnReasonManager;
import combatlogx.expansion.mob.tagger.manager.SpawnReasonManager_Legacy;
import combatlogx.expansion.mob.tagger.manager.SpawnReasonManager_New;

public final class MobTaggerExpansion extends Expansion {
    private final MobTaggerConfiguration configuration;
    private ISpawnReasonManager spawnReasonManager;

    public MobTaggerExpansion(ICombatLogX plugin) {
        super(plugin);
        this.configuration = new MobTaggerConfiguration();
        this.spawnReasonManager = null;
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        reloadConfig();

        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 14) {
            this.spawnReasonManager = new SpawnReasonManager_Legacy(this);
        } else {
            this.spawnReasonManager = new SpawnReasonManager_New(this);
        }

        new ListenerDamage(this).register();
    }

    @Override
    public void onDisable() {
        ISpawnReasonManager spawnReasonManager = getSpawnReasonManager();
        if (spawnReasonManager != null) {
            spawnReasonManager.clear();
        }
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");

        YamlConfiguration yamlConfiguration = configurationManager.get("config.yml");
        MobTaggerConfiguration configuration = getConfiguration();
        configuration.load(yamlConfiguration);
    }

    public @Nullable ISpawnReasonManager getSpawnReasonManager() {
        return this.spawnReasonManager;
    }

    public @NotNull MobTaggerConfiguration getConfiguration() {
        return this.configuration;
    }
}
