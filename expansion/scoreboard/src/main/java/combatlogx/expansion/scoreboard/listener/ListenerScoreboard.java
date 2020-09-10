package combatlogx.expansion.scoreboard.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.SirBlobman.combatlogx.api.event.PlayerCombatTimerChangeEvent;
import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.scoreboard.ScoreboardExpansion;
import combatlogx.expansion.scoreboard.manager.ScoreboardManager;

public class ListenerScoreboard extends ExpansionListener {
    private final ScoreboardExpansion expansion;
    public ListenerScoreboard(ScoreboardExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        updateScoreboard(player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onUpdate(PlayerCombatTimerChangeEvent e) {
        Player player = e.getPlayer();
        updateScoreboard(player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onUntag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        removeScoreboard(player);
    }

    private void updateScoreboard(Player player) {
        ScoreboardManager scoreboardManager = this.expansion.getScoreboardManager();
        scoreboardManager.updateScoreboard(player);
    }

    private void removeScoreboard(Player player) {
        ScoreboardManager scoreboardManager = this.expansion.getScoreboardManager();
        scoreboardManager.removeScoreboard(player);
    }
}