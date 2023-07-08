package combatlogx.expansion.compatibility.iridium.skyblock;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.expansion.skyblock.IslandWrapper;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.User;
import com.iridium.iridiumskyblock.managers.IslandManager;

public class IslandWrapperIridium extends IslandWrapper {
    private Island island;

    public IslandWrapperIridium(@NotNull Island island) {
        this.island = island;
    }

    private @NotNull Island getIsland() {
        return this.island;
    }

    @Override
    public @NotNull Set<UUID> getMembers() {
        Island island = getIsland();
        IslandManager islandManager = getIslandManager();
        List<User> userList = islandManager.getMembersOnIsland(island);

        Set<UUID> memberSet = new HashSet<>();
        for (User user : userList) {
            UUID userId = user.getUuid();
            memberSet.add(userId);
        }

        return Collections.unmodifiableSet(memberSet);
    }

    private @NotNull IridiumSkyblock getSkyBlock() {
        return IridiumSkyblock.getInstance();
    }

    private @NotNull IslandManager getIslandManager() {
        IridiumSkyblock iridiumSkyblock = getSkyBlock();
        return iridiumSkyblock.getIslandManager();
    }
}
