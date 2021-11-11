package combatlogx.expansion.compatibility.mythicmobs;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class MythicMobsExpansion extends Expansion {
    public MythicMobsExpansion(ICombatLogX plugin) {
        super(plugin);
    }
    
    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }
    
    @Override
    public void onEnable() {
        if(!checkDependency("MythicMobs", true, "4.13")) {
            selfDisable();
            return;
        }
        
        new ListenerMythicMobs(this).register();
    }
    
    @Override
    public void onDisable() {
        // Do Nothing
    }
    
    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
    }
}
