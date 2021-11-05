package combatlogx.expansion.compatibility.featherboard;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

import combatlogx.expansion.compatibility.featherboard.listener.ListenerFeatherBoard;

public final class FeatherBoardExpansion extends Expansion {
    public FeatherBoardExpansion(ICombatLogX plugin) {
        super(plugin);
    }
    
    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }
    
    @Override
    public void onEnable() {
        if(!checkDependency("FeatherBoard", true, "5")) {
            ICombatLogX plugin = getPlugin();
            ExpansionManager expansionManager = plugin.getExpansionManager();
            expansionManager.disableExpansion(this);
            return;
        }
        
        new ListenerFeatherBoard(this).register();
    }
    
    @Override
    public void onDisable() {
    
    }
    
    @Override
    public void reloadConfig() {
    
    }
}
