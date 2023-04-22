package combatlogx.expansion.compatibility.citizens.listener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.configuration.CitizensConfiguration;
import combatlogx.expansion.compatibility.citizens.configuration.Configuration;
import combatlogx.expansion.compatibility.citizens.configuration.SentinelConfiguration;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;
import combatlogx.expansion.compatibility.citizens.manager.InventoryManager;
import combatlogx.expansion.compatibility.citizens.object.CombatNPC;
import net.citizensnpcs.api.npc.NPC;

public abstract class CitizensExpansionListener extends ExpansionListener {
    private final CitizensExpansion expansion;

    public CitizensExpansionListener(@NotNull CitizensExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    protected final @NotNull CitizensExpansion getCitizensExpansion() {
        return this.expansion;
    }

    protected final @NotNull Configuration getConfiguration() {
        CitizensExpansion expansion = getCitizensExpansion();
        return expansion.getConfiguration();
    }

    protected final @NotNull CitizensConfiguration getCitizensConfiguration() {
        CitizensExpansion expansion = getCitizensExpansion();
        return expansion.getCitizensConfiguration();
    }

    protected final @NotNull SentinelConfiguration getSentinelConfiguration() {
        CitizensExpansion expansion = getCitizensExpansion();
        return expansion.getSentinelConfiguration();
    }

    protected final @NotNull CombatNpcManager getCombatNpcManager() {
        CitizensExpansion expansion = getCitizensExpansion();
        return expansion.getCombatNpcManager();
    }

    protected final @NotNull InventoryManager getInventoryManager() {
        CitizensExpansion expansion = getCitizensExpansion();
        return expansion.getInventoryManager();
    }

    protected final @Nullable CombatNPC getCombatNPC(NPC npc) {
        CombatNpcManager combatNpcManager = getCombatNpcManager();
        return combatNpcManager.getCombatNPC(npc);
    }
}
