package combatlogx.expansion.compatibility.citizens.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityResurrectEvent;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.configuration.CitizensConfiguration;
import combatlogx.expansion.compatibility.citizens.object.CombatNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public final class ListenerResurrect extends CitizensExpansionListener {
    public ListenerResurrect(CitizensExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onResurrect(EntityResurrectEvent e) {
        CitizensConfiguration configuration = getCitizensConfiguration();
        if (!configuration.isPreventResurrect()) {
            return;
        }

        LivingEntity entity = e.getEntity();
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        NPC npc = npcRegistry.getNPC(entity);
        if (npc == null) {
            return;
        }

        CombatNPC combatNPC = getCombatNPC(npc);
        if (combatNPC == null) {
            return;
        }

        e.setCancelled(true);
    }
}
