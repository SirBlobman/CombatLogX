package combatlogx.expansion.compatibility.region.towny;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyWorld;
import io.github.townyadvanced.flagwar.FlagWarAPI;

public final class TownyRegionHandler extends RegionHandler {
    public TownyRegionHandler(TownyExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.region-protection.towny-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagInformation tagInformation) {
        TagType tagType = tagInformation.getCurrentTagType();
        if (tagType != TagType.PLAYER) {
            return false;
        }

        RegionExpansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        TownBlock townBlock = getTownBlock(location);
        if (townBlock == null) {
            return false;
        }

        if (configuration.getBoolean("prevent-all-town-entries", false)) {
            if (isOwnTown(townBlock, player)) {
                return true;
            }
        }

        TownyWorld townyWorld = townBlock.getWorld();
        if (townyWorld == null || townyWorld.isForcePVP()) {
            return false;
        }

        Town town;
        try {
            town = townBlock.getTown();
            if (town == null || town.isPVP() || town.isAdminEnabledPVP() || town.hasActiveWar()) {
                return false;
            }
        } catch (NotRegisteredException ex) {
            return false;
        }

        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled("FlagWar")) {
            if (FlagWarAPI.isUnderAttack(town)) {
                return false;
            }
        }

        TownyPermission townBlockPermissions = townBlock.getPermissions();
        return !townBlockPermissions.pvp;
    }

    private TownBlock getTownBlock(Location location) {
        TownyAPI townyAPI = TownyAPI.getInstance();
        return townyAPI.getTownBlock(location);
    }

    private boolean isOwnTown(TownBlock townBlock, Player player) {
        if (townBlock == null) {
            return false;
        }

        Town town;
        try {
            town = townBlock.getTown();
        } catch (NotRegisteredException ex) {
            return false;
        }

        List<Resident> residentList = town.getResidents();
        Set<UUID> residentIdSet = new HashSet<>();
        for (Resident resident : residentList) {
            UUID residentId = resident.getUUID();
            residentIdSet.add(residentId);
        }

        UUID playerId = player.getUniqueId();
        return residentIdSet.contains(playerId);
    }
}
