package combatlogx.expansion.compatibility.citizens.listener;

import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.configuration.CitizensConfiguration;
import combatlogx.expansion.compatibility.citizens.configuration.Configuration;
import combatlogx.expansion.compatibility.citizens.configuration.SentinelConfiguration;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;
import combatlogx.expansion.compatibility.citizens.manager.InventoryManager;
import combatlogx.expansion.compatibility.citizens.object.CombatNPC;
import net.citizensnpcs.api.npc.NPC;
import org.jetbrains.annotations.Nullable;

public abstract class CitizensExpansionListener extends ExpansionListener {
    private final CitizensExpansion expansion;

    public CitizensExpansionListener(CitizensExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    protected final CitizensExpansion getCitizensExpansion() {
        return this.expansion;
    }

    protected final Configuration getConfiguration() {
        CitizensExpansion expansion = getCitizensExpansion();
        return expansion.getConfiguration();
    }

    protected final CitizensConfiguration getCitizensConfiguration() {
        CitizensExpansion expansion = getCitizensExpansion();
        return expansion.getCitizensConfiguration();
    }

    protected final SentinelConfiguration getSentinelConfiguration() {
        CitizensExpansion expansion = getCitizensExpansion();
        return expansion.getSentinelConfiguration();
    }

    protected final CombatNpcManager getCombatNpcManager() {
        CitizensExpansion expansion = getCitizensExpansion();
        return expansion.getCombatNpcManager();
    }

    protected final InventoryManager getInventoryManager() {
        CitizensExpansion expansion = getCitizensExpansion();
        return expansion.getInventoryManager();
    }

    @Nullable
    protected final CombatNPC getCombatNPC(NPC npc) {
        CombatNpcManager combatNpcManager = getCombatNpcManager();
        return combatNpcManager.getCombatNPC(npc);
    }
}
