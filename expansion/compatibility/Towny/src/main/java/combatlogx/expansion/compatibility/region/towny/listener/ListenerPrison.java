package combatlogx.expansion.compatibility.region.towny.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import com.palmergames.bukkit.towny.event.resident.ResidentPreJailEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.jail.JailReason;
import combatlogx.expansion.compatibility.region.towny.TownyConfiguration;
import combatlogx.expansion.compatibility.region.towny.TownyExpansion;

public final class ListenerPrison extends ExpansionListener {
    private final TownyExpansion expansion;

    public ListenerPrison(@NotNull TownyExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPrison(ResidentPreJailEvent e) {
        if (!isPreventJailDuringCombat()) {
            return;
        }

        JailReason reason = e.getReason();
        if (reason != JailReason.MAYOR) {
            return;
        }

        Resident resident = e.getResident();
        Player player = resident.getPlayer();
        if (player == null) {
            return;
        }

        if (isInCombat(player)) {
            e.setCancelled(true);
        }
    }

    private boolean isPreventJailDuringCombat() {
        TownyConfiguration configuration = this.expansion.getTownyConfiguration();
        return configuration.isPreventJailDuringCombat();
    }
}
