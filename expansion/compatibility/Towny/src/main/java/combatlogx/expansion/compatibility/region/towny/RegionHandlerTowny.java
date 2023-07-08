package combatlogx.expansion.compatibility.region.towny;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyWorld;
import io.github.townyadvanced.flagwar.FlagWarAPI;

public final class RegionHandlerTowny extends RegionHandler<TownyExpansion> {
    public RegionHandlerTowny(@NotNull TownyExpansion expansion) {
        super(expansion);
    }

    @Override
    public @NotNull String getEntryDeniedMessagePath(@NotNull TagType tagType) {
        return "expansion.region-protection.towny-no-entry";
    }

    @Override
    public boolean isSafeZone(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        TagType tagType = tag.getCurrentTagType();
        if (tagType != TagType.PLAYER) {
            return false;
        }

        if (isPreventAllTownEntries() && isOwnTown(player, location)) {
            return true;
        }

        TownyWorld world = getTownyWorld(location);
        if (world != null && world.isForcePVP()) {
            return false;
        }

        Town town = getTown(location);
        if (town == null || town.isPVP() || town.isAdminEnabledPVP() || town.hasActiveWar()) {
            return false;
        }

        if (isUnderAttack(town)) {
            return false;
        }

        TownBlock townBlock = getTownBlock(location);
        if (townBlock == null) {
            return false;
        }

        TownyPermission permissions = townBlock.getPermissions();
        return !permissions.pvp;
    }

    private @NotNull TownyConfiguration getConfiguration() {
        TownyExpansion expansion = getExpansion();
        return expansion.getTownyConfiguration();
    }

    private boolean isPreventAllTownEntries() {
        TownyConfiguration configuration = getConfiguration();
        return configuration.isPreventAllTownEntries();
    }

    private @NotNull TownyAPI getAPI() {
        return TownyAPI.getInstance();
    }

    private @Nullable TownBlock getTownBlock(@NotNull Location location) {
        TownyAPI api = getAPI();
        return api.getTownBlock(location);
    }

    private @Nullable Town getTown(@NotNull Location location) {
        TownBlock townBlock = getTownBlock(location);
        if (townBlock == null) {
            return null;
        }

        return townBlock.getTownOrNull();
    }

    private @Nullable TownyWorld getTownyWorld(@NotNull Location location) {
        TownBlock townBlock = getTownBlock(location);
        if (townBlock == null) {
            return null;
        }

        return townBlock.getWorld();
    }

    private boolean isOwnTown(@NotNull Player player, @NotNull Location location) {
        Town town = getTown(location);
        if (town == null) {
            return false;
        }

        return town.hasResident(player);
    }

    private boolean isUnderAttack(@NotNull Town town) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (!pluginManager.isPluginEnabled("FlagWar")) {
            return false;
        }

        return FlagWarAPI.isUnderAttack(town);
    }
}
