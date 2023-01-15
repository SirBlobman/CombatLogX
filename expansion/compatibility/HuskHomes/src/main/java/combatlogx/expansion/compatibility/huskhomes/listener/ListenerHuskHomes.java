package combatlogx.expansion.compatibility.huskhomes.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

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
        Teleport timedTeleport = e.getTimedTeleport();
        Player player = Bukkit.getPlayer(timedTeleport.teleporter.uuid);
        if (player == null) {
            return;
        }

        if (isInCombat(player)) {
            String messagePath = "expansion.huskhomes-compatibility.prevent-teleport";
            sendMessageWithPrefix(player, messagePath, null);
            e.setCancelled(true);
        }
    }
}
