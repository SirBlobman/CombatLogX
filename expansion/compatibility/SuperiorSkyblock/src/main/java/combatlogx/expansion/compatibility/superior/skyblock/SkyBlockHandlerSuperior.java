package combatlogx.expansion.compatibility.superior.skyblock;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.github.sirblobman.combatlogx.api.expansion.skyblock.IslandWrapper;
import com.github.sirblobman.combatlogx.api.expansion.skyblock.SkyBlockHandler;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

public final class SkyBlockHandlerSuperior extends SkyBlockHandler<SuperiorSkyblockExpansion> {
    public SkyBlockHandlerSuperior(@NotNull SuperiorSkyblockExpansion expansion) {
        super(expansion);
    }

    @Override
    public @Nullable IslandWrapper getIsland(@NotNull Location location) {
        return wrap(SuperiorSkyblockAPI.getIslandAt(location));
    }

    @Override
    public @Nullable IslandWrapper getIsland(@NotNull OfflinePlayer player) {
        return wrap(getUnwrappedIsland(player));
    }

    @Override
    public boolean doesIslandMatch(@NotNull OfflinePlayer player1, @NotNull OfflinePlayer player2) {
        Island island1 = getUnwrappedIsland(player1);
        Island island2 = getUnwrappedIsland(player2);
        if (island1 == null || island2 == null) {
            return false;
        }

        UUID islandId1 = island1.getUniqueId();
        UUID islandId2 = island2.getUniqueId();
        return islandId1.equals(islandId2);
    }

    private @Nullable SuperiorPlayer getUser(@NotNull OfflinePlayer player) {
        UUID playerId = player.getUniqueId();
        return SuperiorSkyblockAPI.getPlayer(playerId);
    }

    private @Nullable Island getUnwrappedIsland(@NotNull OfflinePlayer player) {
        SuperiorPlayer user = getUser(player);
        if (user == null) {
            return null;
        }

        return user.getIsland();
    }

    private @Nullable IslandWrapper wrap(@Nullable Island island) {
        if (island == null) {
            return null;
        }

        return new IslandWrapperSuperior(island);
    }
}
