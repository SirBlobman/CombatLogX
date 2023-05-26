package combatlogx.expansion.force.field.task;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.force.field.ForceFieldExpansion;
import combatlogx.expansion.force.field.configuration.ForceFieldConfiguration;

public final class ListenerForceField extends ExpansionListener {
    private final ForceFieldExpansion expansion;
    private final Map<UUID, ForceFieldPlayerTask> taskMap;

    public ListenerForceField(@NotNull ForceFieldExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
        this.taskMap = new HashMap<>();
    }

    public @NotNull ForceFieldExpansion getForceFieldExpansion() {
        return this.expansion;
    }

    public @Nullable ForceFieldPlayerTask getTask(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        return this.taskMap.get(playerId);
    }

    public void removeTask(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        ForceFieldPlayerTask task = this.taskMap.remove(playerId);
        if (task != null && !task.isCancelled()) {
            task.removeForceField(player);
            task.cancel();
        }
    }

    public void registerTask(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        ForceFieldExpansion expansion = getForceFieldExpansion();
        ForceFieldPlayerTask task = new ForceFieldPlayerTask(expansion, player);
        this.taskMap.put(playerId, task);
        task.setPeriod(1L);

        TaskScheduler scheduler = getJavaPlugin().getFoliaHelper().getScheduler();
        scheduler.scheduleEntityTask(task);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTag(PlayerTagEvent e) {
        ForceFieldConfiguration configuration = getForceFieldExpansion().getConfiguration();
        if (configuration.isEnabled()) {
            Player player = e.getPlayer();
            removeTask(player);
            registerTask(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        removeTask(player);
    }
}
