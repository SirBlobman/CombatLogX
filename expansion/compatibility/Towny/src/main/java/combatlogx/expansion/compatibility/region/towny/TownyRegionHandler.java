package combatlogx.expansion.compatibility.region.towny;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyWorld;
import io.github.townyadvanced.flagwar.FlagWarAPI;

public class TownyRegionHandler extends RegionHandler {
    public TownyRegionHandler(TownyExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.towny-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        if(tagType != TagType.PLAYER) return false;

        RegionExpansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        TownBlock townBlock = getTownBlock(location);
        if(configuration.getBoolean("prevent-all-town-entries", false)
                && isOwnTown(townBlock, player)) {
            return true;
        }

        TownyAPI townyAPI = TownyAPI.getInstance();
        if(townyAPI.isWarTime()) return false;

        TownyWorld townyWorld = townBlock.getWorld();
        if(townyWorld == null || townyWorld.isForcePVP()) return false;

        Town town = townBlock.getTownOrNull();
        if(town == null || town.isPVP() || town.isAdminEnabledPVP()) return false;
        if(FlagWarAPI.isUnderAttack(town)) return false;

        TownyPermission townBlockPermissions = townBlock.getPermissions();
        return !townBlockPermissions.pvp;
    }

    private TownBlock getTownBlock(Location location) {
        TownyAPI townyAPI = TownyAPI.getInstance();
        return townyAPI.getTownBlock(location);
    }

    private boolean isOwnTown(TownBlock townBlock, Player player) {
        if(townBlock == null) return false;

        Town town = townBlock.getTownOrNull();
        if(town == null) return false;

        List<Resident> residentList = town.getResidents();
        Set<UUID> residentIdList = residentList.stream().map(Resident::getUUID).collect(Collectors.toSet());

        UUID playerId = player.getUniqueId();
        return residentIdList.contains(playerId);
    }
}
