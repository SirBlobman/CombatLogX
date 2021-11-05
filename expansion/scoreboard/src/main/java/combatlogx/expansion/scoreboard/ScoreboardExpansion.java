package combatlogx.expansion.scoreboard;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.manager.ITimerManager;

import combatlogx.expansion.scoreboard.manager.CustomScoreboardManager;

public final class ScoreboardExpansion extends Expansion {
    private final CustomScoreboardManager scoreboardManager;
    
    public ScoreboardExpansion(ICombatLogX plugin) {
        super(plugin);
        this.scoreboardManager = new CustomScoreboardManager(this);
    }
    
    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }
    
    @Override
    public void onEnable() {
        ICombatLogX plugin = getPlugin();
        ITimerManager timerManager = plugin.getTimerManager();
        timerManager.addUpdaterTask(new ScoreboardUpdater(this));
    }
    
    @Override
    public void onDisable() {
        CustomScoreboardManager scoreboardManager = getScoreboardManager();
        scoreboardManager.removeAll();
    }
    
    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
    }
    
    public CustomScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }
}
