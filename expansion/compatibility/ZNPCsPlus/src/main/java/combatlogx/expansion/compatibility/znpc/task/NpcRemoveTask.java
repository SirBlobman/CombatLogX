package combatlogx.expansion.compatibility.znpc.task;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.folia.details.TaskDetails;

import combatlogx.expansion.compatibility.znpc.ZNPCExpansion;
import lol.pyr.znpcsplus.api.NpcApiProvider;
import lol.pyr.znpcsplus.api.npc.Npc;
import lol.pyr.znpcsplus.api.npc.NpcRegistry;

public final class NpcRemoveTask extends TaskDetails {
    private final Npc npc;

    public NpcRemoveTask(@NotNull ZNPCExpansion expansion, @NotNull Npc npc) {
        super(expansion.getPlugin().getPlugin());
        this.npc = npc;
    }

    @Override
    public void run() {
        this.npc.setEnabled(false);

        NpcRegistry npcRegistry = NpcApiProvider.get().getNpcRegistry();
        String npcId = npcRegistry.getByUuid(this.npc.getUuid()).getId();
        npcRegistry.delete(npcId);
    }
}
