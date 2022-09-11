package combatlogx.expansion.force.field;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.force.field.configuration.ForceFieldConfiguration;
import combatlogx.expansion.force.field.task.ForceFieldTask;
import org.jetbrains.annotations.Nullable;

public final class ForceFieldExpansion extends Expansion {
    private final ForceFieldConfiguration configuration;
    private ForceFieldTask task;

    public ForceFieldExpansion(ICombatLogX plugin) {
        super(plugin);
        this.configuration = new ForceFieldConfiguration();
        this.task = null;
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        reloadConfig();

        if (!checkDependency("ProtocolLib", true, "5")) {
            selfDisable();
            return;
        }

        registerTask();
    }

    @Override
    public void onDisable() {
        ForceFieldTask task = getTask();
        if(task == null) {
            return;
        }

        task.cancel();
        task.removeProtocol();
        this.task = null;
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");

        ForceFieldConfiguration forceFieldConfiguration = getConfiguration();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        forceFieldConfiguration.load(configuration);
    }

    @Nullable
    public ForceFieldTask getTask() {
        return this.task;
    }

    public ForceFieldConfiguration getConfiguration() {
        return this.configuration;
    }

    private void registerTask() {
        this.task = new ForceFieldTask(this);
        this.task.register();
        this.task.registerTask();
        this.task.registerProtocol();
    }
}
