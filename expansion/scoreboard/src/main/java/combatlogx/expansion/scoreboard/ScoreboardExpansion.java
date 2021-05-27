package combatlogx.expansion.scoreboard;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ITimerManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.scoreboard.manager.ScoreboardManager;

public class ScoreboardExpansion extends Expansion {
    private final ScoreboardManager scoreboardManager;
    public ScoreboardExpansion(ICombatLogX plugin) {
        super(plugin);
        this.scoreboardManager = new ScoreboardManager(this);
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
        ScoreboardManager scoreboardManager = getScoreboardManager();
        scoreboardManager.removeAll();
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
    }

    public ScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }
}
