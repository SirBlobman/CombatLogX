package combatlogx.expansion.compatibility.fabled.skyblock;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.github.sirblobman.combatlogx.api.expansion.skyblock.IslandWrapper;
import com.github.sirblobman.combatlogx.api.expansion.skyblock.SkyBlockHandler;

import com.songoda.skyblock.api.SkyBlockAPI;
import com.songoda.skyblock.api.island.Island;
import com.songoda.skyblock.api.island.IslandManager;

public final class SkyBlockHandlerFabled extends SkyBlockHandler<FabledSkyBlockExpansion> {
    public SkyBlockHandlerFabled(@NotNull FabledSkyBlockExpansion expansion) {
        super(expansion);
    }

    @Override
    public @Nullable IslandWrapper getIsland(@NotNull Location location) {
        IslandManager islandManager = getIslandManager();
        return wrap(islandManager.getIslandAtLocation(location));
    }

    @Override
    public @NotNull IslandWrapper getIsland(@NotNull OfflinePlayer player) {
        IslandManager islandManager = getIslandManager();
        return wrap(islandManager.getIsland(player));
    }

    @Override
    public boolean doesIslandMatch(@NotNull OfflinePlayer player1, @NotNull OfflinePlayer player2) {
        IslandManager islandManager = getIslandManager();
        Island island1 = islandManager.getIsland(player1);
        Island island2 = islandManager.getIsland(player2);

        if (island1 == null || island2 == null) {
            return false;
        }

        UUID islandId1 = island1.getIslandUUID();
        UUID islandId2 = island2.getIslandUUID();
        return islandId1.equals(islandId2);
    }

    private @NotNull IslandManager getIslandManager() {
        return SkyBlockAPI.getIslandManager();
    }

    private @Nullable IslandWrapper wrap(@Nullable Island island) {
        if (island != null) {
            return new IslandWrapperFabled(island);
        }

        return null;
    }
}
