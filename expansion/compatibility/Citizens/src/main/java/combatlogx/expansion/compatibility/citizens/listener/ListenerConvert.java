package combatlogx.expansion.compatibility.citizens.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTransformEvent;

import com.github.sirblobman.combatlogx.api.utility.EntityHelper;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.object.CombatNPC;
import net.citizensnpcs.api.npc.NPC;

public final class ListenerConvert extends CitizensExpansionListener {
    public ListenerConvert(@NotNull CitizensExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onConvert(EntityTransformEvent e) {
        Entity entity = e.getEntity();
        if (!EntityHelper.isNPC(entity)) {
            return;
        }

        NPC npc = getNPC(entity);
        if (npc == null) {
            return;
        }

        CombatNPC combatNPC = getCombatNPC(npc);
        if (combatNPC != null) {
            e.setCancelled(true);
        }
    }
}
