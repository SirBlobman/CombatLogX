package com.github.sirblobman.combatlogx.task;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.folia.details.TaskDetails;
import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

/**
 * This task is used to untag players from combat. It runs every 10 ticks.
 */
public final class UntagTask extends TaskDetails<ConfigurablePlugin> implements Runnable {
    private final ICombatLogX plugin;

    public UntagTask(@NotNull ICombatLogX plugin) {
        super(plugin.getPlugin());
        this.plugin = plugin;
    }

    public void register() {
        ICombatLogX plugin = getCombatLogX();
        TaskScheduler<ConfigurablePlugin> scheduler = plugin.getFoliaHelper().getScheduler();

        setDelay(5L);
        setPeriod(10L);
        scheduler.scheduleTask(this);
    }

    @Override
    public void run() {
        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        List<Player> playerCombatList = combatManager.getPlayersInCombat();

        for (Player player : playerCombatList) {
            TagInformation tagInformation = combatManager.getTagInformation(player);
            if (tagInformation != null && tagInformation.isExpired()) {
                combatManager.untag(player, UntagReason.EXPIRE);
            }
        }
    }

    private @NotNull ICombatLogX getCombatLogX() {
        return this.plugin;
    }
}
