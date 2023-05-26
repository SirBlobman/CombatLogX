package combatlogx.expansion.force.field;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.force.field.configuration.ForceFieldConfiguration;
import combatlogx.expansion.force.field.task.ForceFieldTask;

public final class ForceFieldExpansion extends Expansion {
    private final ForceFieldConfiguration configuration;
    private ForceFieldTask task;

    public ForceFieldExpansion(@NotNull ICombatLogX plugin) {
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
        if (!checkDependency("ProtocolLib", true, "5")) {
            selfDisable();
            return;
        }

        reloadConfig();
        registerTask();
    }

    @Override
    public void onDisable() {
        ForceFieldTask task = getTask();
        if (task == null) {
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
        getConfiguration().load(configurationManager.get("config.yml"));
    }

    public @Nullable ForceFieldTask getTask() {
        return this.task;
    }

    public @NotNull ForceFieldConfiguration getConfiguration() {
        return this.configuration;
    }

    private void registerTask() {
        this.task = new ForceFieldTask(this);
        this.task.register();
        this.task.registerTask();
        this.task.registerProtocol();
    }
}
