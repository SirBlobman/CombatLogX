package combatlogx.expansion.compatibility.citizens.listener;

import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;
import com.SirBlobman.combatlogx.api.expansion.ExpansionListener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityResurrectEvent;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;
import combatlogx.expansion.compatibility.citizens.object.CombatNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public final class ListenerResurrect extends ExpansionListener {
    private final CitizensExpansion expansion;
    public ListenerResurrect(CitizensExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onResurrect(EntityResurrectEvent e) {
        YamlConfiguration configuration = getConfiguration();
        if(!configuration.getBoolean("prevent-resurrect")) return;

        LivingEntity entity = e.getEntity();
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        NPC npc = npcRegistry.getNPC(entity);
        if(npc == null) return;

        CombatNpcManager combatNpcManager = this.expansion.getCombatNpcManager();
        CombatNPC combatNPC = combatNpcManager.getCombatNPC(npc);
        if(combatNPC == null) return;

        e.setCancelled(true);
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("citizens.yml");
    }
}