package combatlogx.expansion.force.field;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class ForceFieldExpansion extends Expansion {
    private final ListenerForceField listenerForceField;

    public ForceFieldExpansion(ICombatLogX plugin) {
        super(plugin);
        this.listenerForceField = new ListenerForceField(this);
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        ListenerForceField listenerForceField = getListenerForceField();
        if(listenerForceField.isEnabled()) {
            listenerForceField.register();
            listenerForceField.registerProtocol();
        }
    }

    @Override
    public void onDisable() {
        ListenerForceField listenerForceField = getListenerForceField();
        listenerForceField.unregister();
        listenerForceField.removeProtocol();
        listenerForceField.clearData();
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");

        ListenerForceField listenerForceField = getListenerForceField();
        listenerForceField.unregister();
        listenerForceField.removeProtocol();
        listenerForceField.clearData();

        if(listenerForceField.isEnabled()) {
            listenerForceField.register();
            listenerForceField.registerProtocol();
        }
    }

    public ListenerForceField getListenerForceField() {
        return this.listenerForceField;
    }
}
