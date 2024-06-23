package combatlogx.expansion.compatibility.znpc.listener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Entity;

import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.compatibility.znpc.ZNPCExpansion;
import combatlogx.expansion.compatibility.znpc.configuration.Configuration;
import combatlogx.expansion.compatibility.znpc.configuration.NpcConfiguration;
import lol.pyr.znpcsplus.api.NpcApiProvider;
import lol.pyr.znpcsplus.api.npc.Npc;
import lol.pyr.znpcsplus.api.npc.NpcEntry;
import lol.pyr.znpcsplus.api.npc.NpcRegistry;

public abstract class NpcExpansionListener extends ExpansionListener {
    private final ZNPCExpansion expansion;

    public NpcExpansionListener(@NotNull ZNPCExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    protected final @NotNull ZNPCExpansion getNpcExpansion() {
        return this.expansion;
    }

    protected final @NotNull Configuration getConfiguration() {
        return getNpcExpansion().getConfiguration();
    }

    protected final @NotNull NpcConfiguration getNpcConfiguration() {
        return getNpcExpansion().getNpcConfiguration();
    }

    protected final @NotNull CombatNpcManager getCombatNpcManager() {
        return getNpcExpansion().getCombatNpcManager();
    }

    protected final @NotNull InventoryManager getInventoryManager() {
        return getNpcExpansion().getInventoryManager();
    }

    protected final @Nullable CombatNPC getCombatNpc(@NotNull NPC npc) {
        return getCombatNpcManager().getCombatNpc(npc);
    }

    protected final @Nullable Npc getNpc(@NotNull Entity entity) {
        NpcRegistry registry = NpcApiProvider.get().getNpcRegistry();
        NpcEntry entry = registry.getByUuid(entity.getUniqueId());
        return entry.getNpc();
    }
}
