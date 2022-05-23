package combatlogx.expansion.force.field;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

import combatlogx.expansion.force.field.listener.ListenerForceField;

public final class ForceFieldExpansion extends Expansion {
    private ListenerForceField listenerForceField;
    private boolean successfullyEnabled;
    
    public ForceFieldExpansion(ICombatLogX plugin) {
        super(plugin);
        this.listenerForceField = null;
        this.successfullyEnabled = false;
    }
    
    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }
    
    @Override
    public void onEnable() {
        if(!checkDependency("ProtocolLib", true)) {
            ExpansionManager expansionManager = getPlugin().getExpansionManager();
            expansionManager.disableExpansion(this);
            return;
        }
        
        registerForceFieldListener();
        this.successfullyEnabled = true;
    }
    
    @Override
    public void onDisable() {
        if(this.successfullyEnabled) {
            ListenerForceField listenerForceField = getListenerForceField();
            listenerForceField.unregister();
            listenerForceField.removeProtocol();
            listenerForceField.clearData();
        }
        
        this.successfullyEnabled = false;
    }
    
    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
        
        ListenerForceField listenerForceField = getListenerForceField();
        listenerForceField.unregister();
        listenerForceField.removeProtocol();
        listenerForceField.clearData();
        
        registerForceFieldListener();
    }
    
    public ListenerForceField getListenerForceField() {
        if(this.listenerForceField == null) {
            this.listenerForceField = new ListenerForceField(this);
        }
        
        return this.listenerForceField;
    }
    
    private void registerForceFieldListener() {
        ListenerForceField listenerForceField = getListenerForceField();
        if(listenerForceField.isEnabled()) {
            listenerForceField.register();
            listenerForceField.registerProtocol();
            listenerForceField.onReload();
        }
    }
}
