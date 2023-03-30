package combatlogx.expansion.compatibility.citizens.listener;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.utility.EntityHelper;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.configuration.Configuration;

public final class ListenerCombat extends CitizensExpansionListener {
    public ListenerCombat(CitizensExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeTag(PlayerPreTagEvent e) {
        printDebug("Detected PlayerPreTagEvent....");

        Configuration configuration = getConfiguration();
        if (configuration.isNpcTagging()) {
            printDebug("NPC tagging is allowed, ignoring event.");
            return;
        }

        Entity entity = e.getEnemy();
        if (EntityHelper.isNPC(entity)) {
            printDebug("enemy is an NPC, cancelling event.");
            e.setCancelled(true);
        }
    }
}
