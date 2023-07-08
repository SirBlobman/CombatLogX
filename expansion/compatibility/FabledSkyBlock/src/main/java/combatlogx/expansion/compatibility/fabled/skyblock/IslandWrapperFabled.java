package combatlogx.expansion.compatibility.fabled.skyblock;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.OfflinePlayer;

import com.github.sirblobman.combatlogx.api.expansion.skyblock.IslandWrapper;

import com.songoda.skyblock.api.island.Island;

public final class IslandWrapperFabled extends IslandWrapper {
    private final Island island;

    public IslandWrapperFabled(@NotNull Island island) {
        this.island = island;
    }

    private @NotNull Island getIsland() {
        return this.island;
    }

    @Override
    public boolean isMember(@NotNull OfflinePlayer player) {
        Island island = getIsland();
        UUID ownerId = island.getOwnerUUID();
        if (player.getUniqueId().equals(ownerId)) {
            return true;
        }

        return island.isCoopPlayer(player);
    }

    @Override
    public @NotNull Set<UUID> getMembers() {
        Island island = getIsland();
        UUID islandOwner = island.getOwnerUUID();

        Set<UUID> memberSet = new HashSet<>(1);
        memberSet.add(islandOwner);
        memberSet.addAll(island.getCoopPlayers().keySet());
        return Collections.unmodifiableSet(memberSet);
    }
}
