package combatlogx.expansion.action.bar;

import java.util.logging.Logger;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.combatlogx.api.manager.ITimerManager;

public final class ActionBarExpansion extends Expansion {
    public ActionBarExpansion(ICombatLogX plugin) {
        super(plugin);
    }
    
    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }
    
    @Override
    public void onEnable() {
        ICombatLogX plugin = getPlugin();
        int minorVersion = VersionUtility.getMinorVersion();
        if(minorVersion < 8) {
            Logger logger = getLogger();
            logger.warning("This expansion requires Spigot 1.8.8 or higher.");
            
            ExpansionManager expansionManager = plugin.getExpansionManager();
            expansionManager.disableExpansion(this);
            return;
        }
        
        ITimerManager timerManager = plugin.getTimerManager();
        timerManager.addUpdaterTask(new ActionBarUpdater(this));
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
