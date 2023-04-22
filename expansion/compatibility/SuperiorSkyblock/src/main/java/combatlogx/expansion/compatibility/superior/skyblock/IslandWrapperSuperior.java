package combatlogx.expansion.compatibility.superior.skyblock;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.expansion.skyblock.IslandWrapper;

import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

public class IslandWrapperSuperior extends IslandWrapper {
    private final Island island;

    public IslandWrapperSuperior(@NotNull Island island) {
        this.island = island;
    }

    private @NotNull Island getIsland() {
        return this.island;
    }

    @Override
    public @NotNull Set<UUID> getMembers() {
        Island island = getIsland();
        List<SuperiorPlayer> islandMemberList = island.getIslandMembers(true);

        Set<UUID> memberSet = new HashSet<>();
        for (SuperiorPlayer member : islandMemberList) {
            UUID memberId = member.getUniqueId();
            memberSet.add(memberId);
        }

        return Collections.unmodifiableSet(memberSet);
    }
}
