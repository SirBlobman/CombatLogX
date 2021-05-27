package combatlogx.expansion.scoreboard;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.object.TimerUpdater;

import combatlogx.expansion.scoreboard.manager.ScoreboardManager;

public final class ScoreboardUpdater implements TimerUpdater {
    private final ScoreboardExpansion expansion;
    public ScoreboardUpdater(ScoreboardExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    private ScoreboardManager getScoreboardManager() {
        return this.expansion.getScoreboardManager();
    }

    @Override
    public void update(Player player, long timeLeftMillis) {
        ScoreboardManager scoreboardManager = getScoreboardManager();
        scoreboardManager.updateScoreboard(player);
    }

    @Override
    public void remove(Player player) {
        ScoreboardManager scoreboardManager = getScoreboardManager();
        scoreboardManager.removeScoreboard(player);
    }
}
