package combatlogx.expansion.compatibility.iridium.skyblock;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.github.sirblobman.combatlogx.api.expansion.skyblock.IslandWrapper;
import com.github.sirblobman.combatlogx.api.expansion.skyblock.SkyBlockHandler;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.User;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.managers.UserManager;

public final class SkyBlockHandlerIridium extends SkyBlockHandler<IridiumSkyblockExpansion> {
    public SkyBlockHandlerIridium(@NotNull IridiumSkyblockExpansion expansion) {
        super(expansion);
    }

    @Override
    public @Nullable IslandWrapper getIsland(@NotNull Location location) {
        IslandManager islandManager = getIslandManager();
        Optional<Island> optionalIsland = islandManager.getTeamViaLocation(location);
        if (optionalIsland.isPresent()) {
            Island island = optionalIsland.get();
            return wrap(island);
        }

        return null;
    }

    @Override
    public @Nullable IslandWrapper getIsland(@NotNull OfflinePlayer player) {
        User user = getUser(player);
        Optional<Island> optionalIsland = user.getIsland();
        if (optionalIsland.isPresent()) {
            Island island = optionalIsland.get();
            return wrap(island);
        }

        return null;
    }

    @Override
    public boolean doesIslandMatch(@NotNull OfflinePlayer player1, @NotNull OfflinePlayer player2) {
        User user1 = getUser(player1);
        User user2 = getUser(player2);

        Optional<Island> optionalIsland1 = user1.getIsland();
        Optional<Island> optionalIsland2 = user2.getIsland();
        if (!optionalIsland1.isPresent() || !optionalIsland2.isPresent()) {
            return false;
        }

        Island island1 = optionalIsland1.get();
        Island island2 = optionalIsland2.get();

        int islandId1 = island1.getId();
        int islandId2 = island2.getId();
        return (islandId1 == islandId2);
    }

    private @NotNull IridiumSkyblock getAPI() {
        return IridiumSkyblock.getInstance();
    }

    private @NotNull IslandManager getIslandManager() {
        IridiumSkyblock api = getAPI();
        return api.getIslandManager();
    }

    private @NotNull UserManager getUserManager() {
        IridiumSkyblock api = getAPI();
        return api.getUserManager();
    }

    private @NotNull User getUser(@NotNull OfflinePlayer player) {
        UserManager userManager = getUserManager();
        return userManager.getUser(player);
    }

    private @NotNull IslandWrapper wrap(@NotNull Island island) {
        return new IslandWrapperIridium(island);
    }
}
