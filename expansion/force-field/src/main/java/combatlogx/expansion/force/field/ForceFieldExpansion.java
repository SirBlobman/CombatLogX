package combatlogx.expansion.force.field;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import combatlogx.expansion.force.field.configuration.ForceFieldConfiguration;
import combatlogx.expansion.force.field.task.ForceFieldAdapter;
import combatlogx.expansion.force.field.task.ListenerForceField;

public final class ForceFieldExpansion extends Expansion {
    private final ForceFieldConfiguration configuration;
    private final ListenerForceField listener;

    public ForceFieldExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.configuration = new ForceFieldConfiguration();
        this.listener = new ListenerForceField(this);
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
        this.listener.register();
        registerProtocol();
    }

    @Override
    public void onDisable() {
        removeProtocol();
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
        getConfiguration().load(configurationManager.get("config.yml"));
    }

    public @NotNull ForceFieldConfiguration getConfiguration() {
        return this.configuration;
    }

    private void registerProtocol() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        ForceFieldAdapter forceFieldAdapter = ForceFieldAdapter.createAdapter(this.listener);
        protocolManager.addPacketListener(forceFieldAdapter);
    }

    private void removeProtocol() {
        ConfigurablePlugin plugin = getPlugin().getPlugin();
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.removePacketListeners(plugin);
    }
}
