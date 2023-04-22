package combatlogx.expansion.compatibility.uskyblock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.combatlogx.api.expansion.skyblock.IslandWrapper;
import com.github.sirblobman.combatlogx.api.expansion.skyblock.SkyBlockHandler;

import us.talabrek.ultimateskyblock.api.IslandInfo;
import us.talabrek.ultimateskyblock.api.uSkyBlockAPI;

public final class SkyBlockHandler_uSkyBlock extends SkyBlockHandler<uSkyBlockExpansion> {
    public SkyBlockHandler_uSkyBlock(@NotNull uSkyBlockExpansion expansion) {
        super(expansion);
    }

    @Override
    public @Nullable IslandWrapper getIsland(@NotNull Location location) {
        return wrap(getIslandInfo(location));
    }

    @Override
    public @Nullable IslandWrapper getIsland(@NotNull OfflinePlayer player) {
        return wrap(getIslandInfo(player));
    }

    @Override
    public boolean doesIslandMatch(@NotNull OfflinePlayer player1, @NotNull OfflinePlayer player2) {
        IslandInfo island1 = getIslandInfo(player1);
        IslandInfo island2 = getIslandInfo(player2);
        if (island1 == null || island2 == null) {
            return false;
        }

        String islandName1 = island1.getName();
        String islandName2 = island2.getName();
        return islandName1.equals(islandName2);
    }

    private @NotNull uSkyBlockAPI getAPI() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin plugin = pluginManager.getPlugin("uSkyBlock");
        return (uSkyBlockAPI) plugin;
    }

    private @Nullable IslandInfo getIslandInfo(@NotNull OfflinePlayer player) {
        if (!player.isOnline()) {
            return null;
        }

        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            return null;
        }

        uSkyBlockAPI api = getAPI();
        return api.getIslandInfo(onlinePlayer);
    }

    private @Nullable IslandInfo getIslandInfo(@NotNull Location location) {
        uSkyBlockAPI api = getAPI();
        return api.getIslandInfo(location);
    }

    private @Nullable IslandWrapper wrap(@Nullable IslandInfo islandInfo) {
        if (islandInfo == null) {
            return null;
        }

        return new IslandWrapper_uSkyBlock(islandInfo);
    }
}
