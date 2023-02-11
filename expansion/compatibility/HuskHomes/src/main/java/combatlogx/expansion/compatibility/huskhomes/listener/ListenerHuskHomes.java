package combatlogx.expansion.compatibility.huskhomes.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.compatibility.huskhomes.HuskHomesExpansion;
import net.william278.huskhomes.event.TeleportWarmupEvent;
import net.william278.huskhomes.teleport.Teleport;

public final class ListenerHuskHomes extends ExpansionListener {
    public ListenerHuskHomes(HuskHomesExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeTeleport(TeleportWarmupEvent e) {
        printDebug("Detected TeleportWarmupEvent...");

        Teleport timedTeleport = e.getTimedTeleport();
        UUID teleporterId = timedTeleport.teleporter.uuid;
        printDebug("Teleporter ID: " + teleporterId);

        Player player = Bukkit.getPlayer(teleporterId);
        if (player == null) {
            printDebug("Teleporter is not a valid player, ignoring.");
            return;
        }

        if (!isInCombat(player)) {
            printDebug("Player is not in combat, ignoring.");
            return;
        }

        printDebug("Sent message and cancelled event.");
        e.setCancelled(true);

        String messagePath = "expansion.huskhomes-compatibility.prevent-teleport";
        LanguageManager languageManager = getLanguageManager();
        languageManager.sendMessageWithPrefix(player, messagePath);
    }
}
