package combatlogx.expansion.scoreboard;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.object.TimerUpdater;

import combatlogx.expansion.scoreboard.manager.CustomScoreboardManager;

public final class ScoreboardUpdater implements TimerUpdater {
    private final ScoreboardExpansion expansion;

    public ScoreboardUpdater(@NotNull ScoreboardExpansion expansion) {
        this.expansion = expansion;
    }

    @Override
    public void update(@NotNull Player player, long timeLeftMillis) {
        CustomScoreboardManager scoreboardManager = getScoreboardManager();
        scoreboardManager.updateScoreboard(player);
    }

    @Override
    public void remove(@NotNull Player player) {
        CustomScoreboardManager scoreboardManager = getScoreboardManager();
        scoreboardManager.removeScoreboard(player);
    }

    private @NotNull CustomScoreboardManager getScoreboardManager() {
        return this.expansion.getScoreboardManager();
    }
}
