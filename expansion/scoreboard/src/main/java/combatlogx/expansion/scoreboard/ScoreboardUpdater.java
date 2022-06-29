package combatlogx.expansion.scoreboard;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.object.TimerUpdater;

import combatlogx.expansion.scoreboard.manager.CustomScoreboardManager;

public final class ScoreboardUpdater implements TimerUpdater {
    private final ScoreboardExpansion expansion;

    public ScoreboardUpdater(ScoreboardExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    @Override
    public void update(Player player, long timeLeftMillis) {
        CustomScoreboardManager scoreboardManager = getScoreboardManager();
        scoreboardManager.updateScoreboard(player);
    }

    @Override
    public void remove(Player player) {
        CustomScoreboardManager scoreboardManager = getScoreboardManager();
        scoreboardManager.removeScoreboard(player);
    }

    private CustomScoreboardManager getScoreboardManager() {
        return this.expansion.getScoreboardManager();
    }
}
