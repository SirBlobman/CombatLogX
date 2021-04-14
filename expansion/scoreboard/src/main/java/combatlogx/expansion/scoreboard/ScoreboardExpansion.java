package combatlogx.expansion.scoreboard;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.scoreboard.manager.ScoreboardManager;
import combatlogx.expansion.scoreboard.task.TaskScoreboardUpdate;

public class ScoreboardExpansion extends Expansion {
    private final ScoreboardManager scoreboardManager;
    private final TaskScoreboardUpdate taskScoreboardUpdate;
    public ScoreboardExpansion(ICombatLogX plugin) {
        super(plugin);
        this.scoreboardManager = new ScoreboardManager(this);
        this.taskScoreboardUpdate = new TaskScoreboardUpdate(this);
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        this.taskScoreboardUpdate.register();
    }

    @Override
    public void onDisable() {
        this.taskScoreboardUpdate.cancel();
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