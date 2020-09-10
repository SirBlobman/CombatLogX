package combatlogx.expansion.scoreboard;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;

import combatlogx.expansion.scoreboard.listener.ListenerScoreboard;
import combatlogx.expansion.scoreboard.manager.ScoreboardManager;

public class ScoreboardExpansion extends Expansion {
    private final ScoreboardManager scoreboardManager;
    public ScoreboardExpansion(ICombatLogX plugin) {
        super(plugin);
        this.scoreboardManager = new ScoreboardManager(this);
    }

    @Override
    public void onLoad() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        new ListenerScoreboard(this).register();
    }

    @Override
    public void onDisable() {
        ScoreboardManager scoreboardManager = getScoreboardManager();
        scoreboardManager.removeAll();
    }

    @Override
    public void reloadConfig() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
    }

    public ScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }
}