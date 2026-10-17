package combatlogx.expansion.compatibility.citizens.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.sirblobman.api.folia.scheduler.TaskScheduler;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;
import combatlogx.expansion.compatibility.citizens.task.PunishTask;
import combatlogx.expansion.compatibility.citizens.task.RestoreInventoryTask;

/**
 * Restores a kept inventory ('death-inventory: KEEP') after the player respawns from the NPC death punishment.
 */
public final class ListenerRespawn extends CitizensExpansionListener {
    public ListenerRespawn(@NotNull CitizensExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        CombatNpcManager combatNpcManager = getCombatNpcManager();
        YamlConfiguration playerData = combatNpcManager.getData(player);
        if (!playerData.getBoolean(PunishTask.RESTORE_AFTER_RESPAWN_PATH, false)) {
            return;
        }

        printDebug("Player " + player.getName() + " respawned, restoring kept inventory.");
        playerData.set(PunishTask.RESTORE_AFTER_RESPAWN_PATH, null);
        combatNpcManager.saveData(player);

        RestoreInventoryTask task = new RestoreInventoryTask(getCitizensExpansion(), player);
        TaskScheduler scheduler = getCombatLogX().getFoliaHelper().getScheduler();
        scheduler.scheduleEntityTask(task);
    }
}
