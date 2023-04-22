package combatlogx.expansion.compatibility.featherboard.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import combatlogx.expansion.compatibility.featherboard.FeatherBoardConfiguration;
import combatlogx.expansion.compatibility.featherboard.FeatherBoardExpansion;

public final class ListenerFeatherBoard extends ExpansionListener {
    private final FeatherBoardExpansion expansion;

    public ListenerFeatherBoard(@NotNull FeatherBoardExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        showTrigger(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        removeTrigger(player);
    }

    private @NotNull FeatherBoardExpansion getFeatherBoardExpansion() {
        return this.expansion;
    }

    private @NotNull FeatherBoardConfiguration getConfiguration() {
        FeatherBoardExpansion expansion = getFeatherBoardExpansion();
        return expansion.getConfiguration();
    }

    private @NotNull String getTriggerName() {
        FeatherBoardConfiguration configuration = getConfiguration();
        String triggerName = configuration.getTriggerName();
        return (triggerName.isEmpty() ? "combatlogx" : triggerName);
    }

    private void showTrigger(@NotNull Player player) {
        String triggerName = getTriggerName();
        FeatherBoardAPI.showScoreboard(player, triggerName, true);
    }

    private void removeTrigger(@NotNull Player player) {
        String triggerName = getTriggerName();
        FeatherBoardAPI.hideScoreboard(player, triggerName, true);
    }
}
